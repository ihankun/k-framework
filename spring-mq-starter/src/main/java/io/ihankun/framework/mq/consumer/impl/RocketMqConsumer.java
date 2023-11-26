package io.ihankun.framework.mq.consumer.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.ihankun.framework.common.context.DomainContext;
import io.ihankun.framework.common.context.GrayContext;
import io.ihankun.framework.common.context.LoginUserContext;
import io.ihankun.framework.common.utils.spring.ServerStateUtil;
import io.ihankun.framework.common.utils.spring.SpringHelpers;
import io.ihankun.framework.log.constant.TraceLogConstant;
import io.ihankun.framework.log.context.TraceLogContext;
import io.ihankun.framework.log.enums.LogTypeEnum;
import io.ihankun.framework.mq.config.GrayMark;
import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.consumer.AbstractConsumer;
import io.ihankun.framework.mq.consumer.ConsumerListener;
import io.ihankun.framework.mq.consumer.ConsumerListenerConfig;
import io.ihankun.framework.mq.consumer.IMqConsumer;
import io.ihankun.framework.mq.message.MqMsg;
import io.ihankun.framework.mq.message.ReceiveMessage;
import io.ihankun.framework.mq.producer.MqTopic;
import io.ihankun.framework.mq.producer.bean.TopicConfigInfo;
import io.ihankun.framework.mq.rocketmq.RocketMqAdmin;
import io.ihankun.framework.mq.rocketmq.consumer.MqPushConsumer;
import io.ihankun.framework.mq.rocketmq.consumer.strategy.ProdAllocateMessageQueueStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.common.subscription.SubscriptionGroupConfig;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
@Component("rocketmqConsumer")
@Slf4j
public class RocketMqConsumer extends AbstractConsumer implements IMqConsumer {
    private final String TRACE_ID_PREFIX = "MQ-";

    private MqProperties properties;

    List<DefaultMQPushConsumer> consumerList = new ArrayList<>();


    /**
     * 返回属性
     *
     * @return
     */
    @Override
    public MqProperties properties() {
        return properties;
    }

    /**
     * 重置地址
     *
     * @param url
     */
    @Override
    public void restart(String url) {

        if (CollectionUtil.isEmpty(consumerList)) {
            return;
        }

        String namesrvAddr = consumerList.get(0).getNamesrvAddr();

        if (url.equals(namesrvAddr)) {
            return;
        }


        shutdown();
        properties.setUrl(url);
        start(properties);
    }

    @Override
    public void shutdown() {
        if (CollectionUtil.isEmpty(consumerList)) {
            return;
        }
        for (DefaultMQPushConsumer consumer : consumerList) {
            try {
                consumer.shutdown();
                log.info("RocketMqConsumer.shutdown,url={},groupName={}", consumer.getNamesrvAddr(), consumer.getConsumerGroup());
            } catch (Exception e) {
                log.error("RocketMqConsumer.shutdown.exception", e);
            }
        }
        consumerList.clear();
    }

    /**
     * 监听初始化，遍历所有实现ConsumerListener接口的实现，对不同的topic+tags进行注册
     * 注意：统一个topic下不同tags的消费，要设置不同的消费组，否则会发生消费异常
     *
     * @param config 配置
     */
    @Override
    public void start(MqProperties config) {
        this.properties = config;
        Map<String, ConsumerListener> beans = SpringHelpers.context().getBeansOfType(ConsumerListener.class);
        if (MapUtil.isEmpty(beans)) {
            return;
        }
        for (ConsumerListener consumer : beans.values()) {
            try {
                buildConsumer(properties, consumer);
            } catch (Exception e) {
                log.error("RocketMqConsumer.start", e);
            }
        }

    }


    /**
     * 构建消费者任务
     *
     * @param config
     * @param listener
     * @return
     */
    private DefaultMQPushConsumer buildConsumer(MqProperties config, ConsumerListener listener) throws MQClientException {

        // 初始化消费者
        String grayMark = Optional.ofNullable(ServerStateUtil.getGrayMark()).orElse("false");

        String consumerGroupName = listener.config().getConsumerGroupName();

        if (StrUtil.isEmpty(consumerGroupName)) {
            consumerGroupName = SpringHelpers.context().getEnvironment().getProperty("spring.application.name", "default_consumer_group") + listener.subscribeTopic() + listener.subscribeTags();
        }

        // 自定义队列的负载均衡策略
        AllocateMessageQueueStrategy allocateMessageQueueStrategy = dynamicCreateStrategy(config);

        DefaultMQPushConsumer consumer = new MqPushConsumer(consumerGroupName, null, allocateMessageQueueStrategy, grayMark);
        consumer.setInstanceName("consumer-" + UtilAll.getPid());

        consumer.setNamesrvAddr(config.getUrl());
        consumer.setVipChannelEnabled(false);
        if (config.getTimeOut() != null) {
            consumer.setConsumeTimeout(config.getTimeOut());
        }

        // 设置消费者线程
        consumer.setConsumeThreadMin(config.getConsumeThreadMin());
        consumer.setConsumeThreadMax(config.getConsumeThreadMax());
        consumer.setConsumeMessageBatchMaxSize(1);

        // 广播模式设置
        ConsumerListenerConfig consumerListenerConfig = listener.config();
        if (consumerListenerConfig.isBoardCast()) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }


        // 手动创建订阅关系配置信息，用于设置%RETRY%topic的队列大小以便消费端消息重发的灰度实现
        createAdminSubscribe(config, consumerGroupName, listener);

        //设置订阅者
        String subscribeTags = GrayMark.buildConsumerTags(listener.subscribeTags());
        consumer.subscribe(listener.subscribeTopic(), subscribeTags);

        //注册消费者
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> receiverMessage(listener, list, context));
        consumer.start();

        consumerList.add(consumer);

        log.info("RocketMqConsumer.buildConsumer.success,consumer={},groupName={},topic={},tags={}", listener.getClass().getSimpleName(), consumerGroupName, listener.subscribeTopic(), subscribeTags);

        return consumer;

    }

    /**
     * 根据策略路径动态创建策略类，创建失败则使用生产策略类
     *
     * @param properties
     * @return
     */
    private AllocateMessageQueueStrategy dynamicCreateStrategy(MqProperties properties) {
        try {
            Class<?> strageClass = Class.forName(properties.getAllocateQueueStrategy());
            Constructor<?> constructor = strageClass.getDeclaredConstructor(MqProperties.class);
            AllocateMessageQueueStrategy strategy = (AllocateMessageQueueStrategy) constructor.newInstance(properties);
            return strategy;
        } catch (Exception e) {
            log.error("RocketMqConsumer.dynamicCreateStrategy,exception", e);
            return new ProdAllocateMessageQueueStrategy(properties);
        }
    }


    /**
     * 接收消息处理
     *
     * @param listener
     * @param list
     * @param context
     * @return
     */
    private ConsumeConcurrentlyStatus receiverMessage(ConsumerListener listener, List<MessageExt> list, ConsumeConcurrentlyContext context) {

        //消息内容为空，则直接返回消费成功
        if (CollectionUtils.isEmpty(list)) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        String topic = context.getMessageQueue().getTopic();
        //获取 traceId，避免多个消息同时接收，打印出多个消息的 traceId
        String traceId = list.get(0).getUserProperty(TraceLogConstant.TRACE_ID);
        if (list.size() > 1) {
            traceId = list.stream().map(item -> item.getUserProperty(TraceLogConstant.TRACE_ID)).collect(Collectors.joining(","));
        }

        //构造MQ消息
        List<MqMsg> mqMessageList = parseMqMessage(list);
        MqMsg firstMessage = mqMessageList.get(0);
        String mqMessageId = list.get(0).getMsgId();
        String businessMessageId = firstMessage.getMessageId();

        log.info("RocketMqConsumer.receiverMessage,traceId={},topic={},msgSize={},mq.msgId={},business.msgId={},list={}", TRACE_ID_PREFIX + traceId, topic, mqMessageList.size(), mqMessageId, businessMessageId, mqMessageList);


        //构造上下文数据
        MDC.put(TraceLogContext.LOG_TYPE, LogTypeEnum.MQ.getValue());
        DomainContext.mock(firstMessage.getTelnet());
        LoginUserContext.mock(firstMessage.getLoginUserInfo());
        GrayContext.mock(firstMessage.getGray());
        TraceLogContext.set(traceId);


        //构造业务回调的消息格式
        List<ReceiveMessage> businessMessageList = convertToReceiveMessage(listener, mqMessageList);

        //执行业务逻辑
        boolean businessStatus = tryExecuteBusiness(listener, businessMessageList);
        ConsumeConcurrentlyStatus status = businessStatus ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS : ConsumeConcurrentlyStatus.RECONSUME_LATER;


        //清理上下文数据
        MDC.remove(TraceLogContext.LOG_TYPE);
        TraceLogContext.reset();
        DomainContext.clear();
        LoginUserContext.clear();
        GrayContext.clear();


        return status;
    }


    /**
     * 尝试执行业务逻辑处理
     *
     * @param listener
     * @param businessMessageList
     * @return
     */
    private boolean tryExecuteBusiness(ConsumerListener listener, List<ReceiveMessage> businessMessageList) {

        boolean idempotent = listener.config() != null && listener.config().isIdempotent();

        //幂等处理，如果开启幂等校验，则对每个消息设置 redis 幂等信息
        if (idempotent) {
            for (ReceiveMessage message : businessMessageList) {
                //如果未设置幂等key，则以消息id为key
                String idempotentKey = getIdempotentKey(listener.config(), message.getMessageId(), message.getData());
                boolean beConsumed = checkConsumed(idempotentKey);
                message.setBeConsumed(beConsumed);
                if (!beConsumed) {
                    confirmConsumed(idempotentKey);
                }
            }
        }

        //执行业务处理
        boolean status = false;
        try {
            log.info("RocketMqConsumer.tryExecuteBusiness receive: {}", businessMessageList);
            return listener.receive(businessMessageList);
        } catch (Exception e) {
            log.error("RocketMqConsumer.receiverMessage,business.execute.exception,traceId=" + TRACE_ID_PREFIX + TraceLogContext.get() + ",messageId=" + businessMessageList.get(0).getMessageId(), e);
        }

        //处理失败，幂等标识删除
        if (idempotent && !status) {
            for (ReceiveMessage message : businessMessageList) {
                resetConsumed(getIdempotentKey(listener.config(), message.getMessageId(), message.getData()));
            }
        }

        return status;
    }

    private String getGroupName(ConsumerListener<?> listener) {

        String groupName = listener.config().getConsumerGroupName();
        if (StringUtils.isEmpty(groupName)) {
            String applicationName = SpringHelpers.context().getEnvironment().getProperty("spring.application.name", "default_consumer_group");
            groupName = applicationName + listener.subscribeTopic() + listener.subscribeTags();
        }
        return groupName;
    }

    /**
     * 数据格式转换
     *
     * @param list
     * @return
     */
    private List<MqMsg> parseMqMessage(List<MessageExt> list) {
        List<MqMsg> result = new ArrayList<>(list.size());
        for (MessageExt messageExt : list) {
            String body = new String(messageExt.getBody(), Charset.defaultCharset());
            MqMsg msg = JSON.parseObject(body, MqMsg.class);
            result.add(msg);
        }
        return result;
    }


    /**
     * 构造返回消息
     *
     * @param listener
     * @param list
     * @return
     */
    private List<ReceiveMessage> convertToReceiveMessage(ConsumerListener listener, List<MqMsg> list) {

        List<ReceiveMessage> messageList = new ArrayList<>(list.size());
        for (MqMsg msg : list) {
            ReceiveMessage receive = new ReceiveMessage();
            receive.setData(objectToClass(msg.getData(), listener));
            receive.setMessageId(msg.getMessageId());
            receive.setTopic(MqTopic.builder().topic(msg.getTopic()).tags(msg.getTag()).build());
            receive.setBeConsumed(false);
            messageList.add(receive);
        }
        return messageList;
    }

    /**
     * 创建consumer group的订阅信息
     *
     * @param mqProperties
     * @param consumerId
     * @param listener
     */
    private void createAdminSubscribe(MqProperties mqProperties, String consumerId, ConsumerListener<?> listener) {
        DefaultMQAdminExt mqAdminExt = new DefaultMQAdminExt();
        mqAdminExt.setNamesrvAddr(mqProperties.getUrl());
        SubscriptionGroupConfig subConfig = new SubscriptionGroupConfig();
        subConfig.setGroupName(consumerId);
        // 设置retryQueueNums为灰度队列长度的2倍
        subConfig.setRetryQueueNums(mqProperties.getGraySize() * 2);
        try {
            mqAdminExt.start();

            ClusterInfo clusterInfo = RocketMqAdmin.fetchClusterInfo(mqAdminExt);
            if (Objects.isNull(clusterInfo)) {
                mqAdminExt.shutdown();
                return;
            }

            List<TopicConfigInfo> topicConfigInfos = RocketMqAdmin.fetchTopicConfig(mqAdminExt, listener.subscribeTopic(), clusterInfo);
            if (topicConfigInfos.isEmpty()) {
                mqAdminExt.shutdown();
                return;
            }

            for (TopicConfigInfo topicConfigInfo : Optional.of(topicConfigInfos).orElse(Collections.emptyList())) {
                mqAdminExt.createAndUpdateSubscriptionGroupConfig(clusterInfo.getBrokerAddrTable().get(topicConfigInfo.getBrokerNameList().get(0)).selectBrokerAddr(), subConfig);
            }
        } catch (Exception e) {
            log.error("RocketMqConsumer.createAdminSubscribe,exception", e);
        } finally {
            mqAdminExt.shutdown();
        }
    }
}
