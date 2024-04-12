package io.ihankun.framework.mq.producer.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.ihankun.framework.core.context.DomainContext;
import io.ihankun.framework.core.context.GrayContext;
import io.ihankun.framework.core.context.LoginUserContext;
import io.ihankun.framework.core.context.LoginUserInfo;
import io.ihankun.framework.core.id.IdGenerator;
import io.ihankun.framework.core.utils.spring.ServerStateUtil;
import io.ihankun.framework.core.utils.spring.SpringHelpers;
import io.ihankun.framework.log.constant.TraceLogConstant;
import io.ihankun.framework.log.context.TraceLogContext;
import io.ihankun.framework.log.enums.LogTypeEnum;
import io.ihankun.framework.mq.MqAutoConfiguration;
import io.ihankun.framework.mq.config.GrayMark;
import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.constants.MqSendResult;
import io.ihankun.framework.mq.message.MqMsg;
import io.ihankun.framework.mq.producer.IMqProducer;
import io.ihankun.framework.mq.producer.MqSendCallback;
import io.ihankun.framework.mq.producer.MqTopic;
import io.ihankun.framework.mq.producer.bean.TopicConfigInfo;
import io.ihankun.framework.mq.rocketmq.RocketMqAdmin;
import io.ihankun.framework.mq.rocketmq.producer.strategy.impl.GgrayProducerSelectQueueStrategy;
import io.ihankun.framework.mq.rocketmq.producer.strategy.impl.GrayProducerSelectQueueStrategy;
import io.ihankun.framework.mq.rocketmq.producer.strategy.impl.ProdProducerSelectQueueStrategy;
import io.ihankun.framework.mq.rule.MqAccessRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.latency.MQFaultStrategy;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageBatch;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
@Slf4j
public class RocketMqProducer implements IMqProducer {

    private static final String TRACE_ID_PREFIX = "MQ-";

    DefaultMQProducer producer;

    MqProperties mqProperties;

    /**
     * Topic是否创建完成缓存
     * key=nameserver地址
     * value=topic
     */
    private static final Set<String> TOPIC_IS_READY = new ConcurrentHashSet<>();

    private String grayMark;

    @Resource
    private MqAccessRule mqAccessRule;

    @Override
    public void start(MqProperties config) {
        this.mqProperties = config;
        String producerGroupName = SpringHelpers.context().getEnvironment().getProperty("spring.application.name", "default_producer_group");
        // 获取当前环境的标识（true：灰度；false：生产；其他：灰灰度）
        this.grayMark = Optional.ofNullable(ServerStateUtil.getGrayMark()).orElse("others");
        MQFaultStrategy mqFaultStrategy;
        if (Boolean.FALSE.toString().equalsIgnoreCase(grayMark)) {
            // 生产节点逻辑：发送消息到queue[graySize-(n-graySize)]
            mqFaultStrategy = new ProdProducerSelectQueueStrategy(mqProperties.getGraySize());
        } else if (Boolean.TRUE.toString().equalsIgnoreCase(grayMark)) {
            // 灰度节点逻辑：发送消息到queue[0-graySize]
            mqFaultStrategy = new GrayProducerSelectQueueStrategy(mqProperties.getGraySize());
        } else {
            // 灰灰度节点逻辑，发送消息到queue[(n-graySize)-n]
            mqFaultStrategy = new GgrayProducerSelectQueueStrategy(mqProperties.getGraySize());
        }
        producer = new io.ihankun.framework.mq.rocketmq.producer.MqProducer(producerGroupName, mqFaultStrategy, null, grayMark);
        producer.setInstanceName("producer-" + UtilAll.getPid());

        producer.setNamesrvAddr(config.getUrl());
        producer.setVipChannelEnabled(false);
        producer.setRetryTimesWhenSendFailed(config.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(config.getRetryTimesWhenSendFailed());
        //设置默认创建topic的queue个数
        producer.setDefaultTopicQueueNums(config.getWriteQueueNums());
        producer.setSendMsgTimeout(config.getTimeOut());

        log.info("rocket.mq.producer.init:config={}", JSON.toJSONString(config));

        try {
            producer.start();
        } catch (MQClientException e) {
            log.error(e.getMessage(), e);
        }
        log.info("rocket.mq.producer.start.success:config={}", JSON.toJSONString(config));
    }

    @Override
    public MqProperties properties() {
        return mqProperties;
    }

    @Override
    public void restart(String url) {

        if (StrUtil.isEmpty(url)) {
            return;
        }

        if (url.equals(producer.getNamesrvAddr())) {
            return;
        }

        shutdown();
        log.info("RocketMqProducer.resetUrl,producer.shutdown,url={}", producer.getNamesrvAddr());
        this.mqProperties.setUrl(url);
        start(this.mqProperties);
        log.info("RocketMqProducer.resetUrl,producer.restart,url={}", producer.getNamesrvAddr());
    }

    @Override
    public void shutdown() {
        producer.shutdown();
    }

    @Override
    public void init(MqProperties config) {

    }

    @Override
    public <T> MqSendResult sendMsg(MqTopic topic, T data) {
        try {

            MDC.put(TraceLogContext.LOG_TYPE, LogTypeEnum.MQ.getValue());
            assertMessage(topic, data);

            log.info("RocketMqProducer.sendMsg.start,traceId={},topic={},data={}", TRACE_ID_PREFIX + TraceLogContext.get(), topic, data);

            MqMsg msg = buildMsg(topic, data);
            Message message = new Message(topic.getTopic(), topic.getTags(), msg.serialize().getBytes());
            message.putUserProperty(TraceLogConstant.TRACE_ID, TraceLogConstant.getTraceId());
            message.putUserProperty("grayMark", grayMark);
            message.putUserProperty("graySize", String.valueOf(mqProperties.getGraySize()));
            SendResult response = producer.send(message);

            MqSendResult result = new MqSendResult();
            result.setMessageId(new String[]{response.getMsgId()});
            result.setMqMessageId(new String[]{response.getMsgId()});


            log.info("RocketMqProducer.sendMsg.finish,traceId={},result={}", TRACE_ID_PREFIX + TraceLogContext.get(), result);

            return result;
        } catch (Exception e) {
            log.error("RocketMqProducer.sendMsg.exception,topic={},data={},e={}", topic, data, e);
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(TraceLogContext.LOG_TYPE);
        }
    }

    @Override
    public <T> MqSendResult sendBatchMsg(MqTopic topic, List<T> data) {
        try {

            MDC.put(TraceLogContext.LOG_TYPE, LogTypeEnum.MQ.getValue());
            assertMessage(topic, data);

            log.info("RocketMqProducer.sendBatchMsg.start,traceId={},topic={},data={}", TRACE_ID_PREFIX + TraceLogContext.get(), topic, data);

            List<Message> list = new ArrayList<>(data.size());
            List<String> messageId = new ArrayList<>(data.size());
            for (T item : data) {
                MqMsg msg = buildMsg(topic, item);
                messageId.add(msg.getMessageId());
                Message message = new Message(topic.getTopic(), topic.getTags(), msg.serialize().getBytes());
                message.putUserProperty(TraceLogConstant.TRACE_ID, TraceLogConstant.getTraceId());
                message.putUserProperty("grayMark", grayMark);
                message.putUserProperty("graySize", String.valueOf(mqProperties.getGraySize()));
                list.add(message);
            }
            MessageBatch message = MessageBatch.generateFromList(list);
            message.setBody(message.encode());
            message.putUserProperty(TraceLogConstant.TRACE_ID, TraceLogConstant.getTraceId());
            message.putUserProperty("grayMark", grayMark);
            message.putUserProperty("graySize", String.valueOf(mqProperties.getGraySize()));
            SendResult sendResult = producer.send(message);
            MqSendResult result = new MqSendResult();
            result.setMessageId(messageId.toArray(new String[0]));
            result.setMqMessageId(new String[]{sendResult.getMsgId()});

            log.info("RocketMqProducer.sendBatchMsg.finish,traceId={},topic={},result={}", TRACE_ID_PREFIX + TraceLogContext.get(), topic, result);

            return result;
        } catch (Exception e) {
            log.error("RocketMqProducer.sendBatchMsg.exception,topic={},e={}", topic, e);
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(TraceLogContext.LOG_TYPE);
        }
    }

    @Override
    public <T> void sendAsyncMsg(MqTopic topic, T data, MqSendCallback callback) {

        assertMessage(topic, data);

        String traceId = TraceLogContext.get();
        String logTraceId = TRACE_ID_PREFIX + traceId;

        log.info("RocketMqProducer.sendAsyncMsg.start,traceId={},topic={},data={}", logTraceId, topic, data);

        try {

            MqMsg msg = buildMsg(topic, data);

            Message message = new Message(topic.getTopic(), topic.getTags(), msg.serialize().getBytes());
            message.putUserProperty(TraceLogConstant.TRACE_ID, TraceLogConstant.getTraceId());
            message.putUserProperty("grayMark", grayMark);
            message.putUserProperty("graySize", String.valueOf(mqProperties.getGraySize()));
            producer.send(message, new org.apache.rocketmq.client.producer.SendCallback() {
                @Override
                public void onSuccess(SendResult resp) {
                    log.info("RocketMqProducer.sendAsyncMsg.onSuccess,traceId={},topic={},bus.msgId={},mq.msgId={},data={},result={}", logTraceId, topic, msg.getMessageId(), resp.getMsgId(), data, resp);
                    if (callback == null) {
                        return;
                    }
                    MqSendResult result = new MqSendResult();
                    result.setMessageId(new String[]{msg.getMessageId()});
                    result.setMqMessageId(new String[]{resp.getMsgId()});
                    try {
                        callback.success(result);
                    } catch (Exception e) {
                        log.error("RocketMqProducer.sendAsyncMsg.exception,traceId={},topic={},data={},e={}", logTraceId, topic, data, e);
                    }
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("RocketMqProducer.sendAsyncMsg.onException,traceId={},topic={},data={},e={}", logTraceId, topic, data, throwable);
                    try {
                        callback.exception(throwable);
                    } catch (Exception e) {
                        log.error("RocketMqProducer.sendAsyncMsg.exception2,traceId={},topic={},data={},e={}", logTraceId, topic, data, e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("RocketMqProducer.sendAsyncMsg.exception3,traceId={},topic={},data={},e={}", logTraceId, topic, data, e);
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public <T> void sendAsyncBatchMsg(MqTopic topic, List<T> data, MqSendCallback callback) {

        String traceId = TraceLogContext.get();
        String logTraceId = TRACE_ID_PREFIX + traceId;

        assertMessage(topic, data);

        log.info("RocketMqProducer.sendAsyncBatchMsg.start,traceId={},topic={},data={}", logTraceId, topic, data);

        try {


            List<Message> messageList = new ArrayList<>(data.size());
            List<String> messageId = new ArrayList<>(data.size());
            List<String> mqMessageId = new ArrayList<>(data.size());
            for (T item : data) {
                MqMsg msg = buildMsg(topic, item);
                messageId.add(msg.getMessageId());
                Message message = new Message(topic.getTopic(), topic.getTags(), msg.serialize().getBytes());
                message.putUserProperty(TraceLogConstant.TRACE_ID, TraceLogConstant.getTraceId());
                message.putUserProperty("grayMark", grayMark);
                message.putUserProperty("graySize", String.valueOf(mqProperties.getGraySize()));
                messageList.add(message);
            }

            MessageBatch message = MessageBatch.generateFromList(messageList);
            message.setBody(message.encode());

            message.putUserProperty(TraceLogConstant.TRACE_ID, TraceLogConstant.getTraceId());
            message.putUserProperty("grayMark", grayMark);
            message.putUserProperty("graySize", String.valueOf(mqProperties.getGraySize()));
            producer.send(message, new org.apache.rocketmq.client.producer.SendCallback() {
                @Override
                public void onSuccess(SendResult resp) {

                    log.info("RocketMqProducer.sendAsyncBatchMsg.onSuccess,traceId={},mq.msgId={},result={}", logTraceId, resp.getMsgId(), resp);

                    if (callback == null) {
                        return;
                    }
                    MqSendResult result = new MqSendResult();
                    result.setMessageId(messageId.toArray(new String[0]));
                    result.setMqMessageId(mqMessageId.toArray(new String[0]));
                    try {
                        callback.success(result);
                    } catch (Exception e) {
                        log.error("RocketMqProducer.sendAsyncBatchMsg.callback.exception,traceId={},topic={},data={},e={}", logTraceId, topic, data, e);
                    }
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("RocketMqProducer.sendAsyncBatchMsg.onException,traceId={},topic={},data={},e={}", logTraceId, topic, data, throwable);
                    try {
                        callback.exception(throwable);
                    } catch (Exception e) {
                        log.error("RocketMqProducer.sendAsyncBatchMsg.onException.callback,traceId={},topic={},data={},e={}", logTraceId, topic, data, throwable);
                    }
                }
            });
        } catch (Exception e) {
            log.error("RocketMqProducer.sendAsyncBatchMsg.send.exception,topic={},data={},e={}", topic, data, e);
            throw new RuntimeException(e.getMessage());
        }
    }


    private <T> void assertMessage(MqTopic topic, T data) {
        if (producer == null) {
            throw new RuntimeException("producer is null");
        }

        if (data == null) {
            throw new RuntimeException("message is null");
        }

        if (StringUtils.isEmpty(topic.getTopic())) {
            throw new RuntimeException("message.header.topic is null");
        }

        if (!mqAccessRule.auth(topic.getTopic())) {
            throw new RuntimeException("message.header.topic is not allowed");
        }

        if (StringUtils.isEmpty(topic.getTags())) {
            throw new RuntimeException("message.header.tags is null");
        }

        // 检查topic是否已经创建，是否按照配置创建
        try {
            this.checkTopicForReady(topic.getTopic());
        } catch (Exception e) {
            log.info("RocketMqProducer.assertMessage,checkTopicForReady.exception,topic={},e={}", topic, e.getMessage());
        }
    }

    private <T> MqMsg buildMsg(MqTopic topic, T data) {

        try {

            MqMsg msg = new MqMsg();
            msg.setData(data);
            msg.setTopic(topic.getTopic());
            msg.setTag(topic.getTags());
            String gray = GrayContext.get();
            if (StringUtils.isEmpty(gray)) {
                gray = GrayMark.getGrayMark();
                if (MqAutoConfiguration.GRAY_MARK.equals(gray)) {
                    gray = Boolean.TRUE.toString();
                }
                if (StringUtils.isEmpty(gray)) {
                    gray = Boolean.FALSE.toString();
                }
            }
            msg.setGray(gray);
            //设置当前登录用户信息
            LoginUserInfo loginUserInfo = LoginUserContext.get();
            msg.setLoginUserInfo(loginUserInfo);

            String domain = DomainContext.get();

            //设置domain，用于Sass化隔离
            msg.setTelnet(domain);

            msg.setMessageId("rocketmq-" + IdGenerator.ins().generator().toString());

            return msg;

        } catch (Exception e) {
            log.error("RocketMqProducer.buildMsg.exception,topic={},data={},e={}", topic, data, e);
            throw e;
        }
    }

    /**
     * 检查topic是否已经创建，未创建根据参数创建；已创建检查是否和配置一致，不一致则更新topic
     *
     * @param topic
     */
    private void checkTopicForReady(String topic) throws NoSuchAlgorithmException {
        // 仅在首次发送消息时检查
        String cacheKey = producer.getNamesrvAddr() + topic;
        if (!TOPIC_IS_READY.contains(cacheKey)) {

            //授权检查
            if (!mqAccessRule.auth(topic)) {
                throw new RuntimeException("消息发送被拒绝，请联系技术中台组授权");
            }

            // 1、启动rocketmq管理工具
            DefaultMQAdminExt mqAdminExt = this.getAndStartMQAdminExt(topic);
            if (mqAdminExt == null) {
                return;
            }

            // 2、检查topic是否存在
            boolean isExisted = RocketMqAdmin.checkTopicExist(mqAdminExt, topic);
            // 如果存在则检查queue配置是否正确
            List<TopicConfigInfo> needCreateOrUpdateTopicConfigInfos = Lists.newArrayList();
            ClusterInfo clusterInfo = RocketMqAdmin.fetchClusterInfo(mqAdminExt);
            if (Objects.isNull(clusterInfo)) {
                mqAdminExt.shutdown();
                return;
            }


            if (isExisted) {
                // 3、获取当前topic相关配置
                List<TopicConfigInfo> topicConfigInfos = RocketMqAdmin.fetchTopicConfig(mqAdminExt, topic, clusterInfo);
                if (topicConfigInfos.isEmpty()) {
                    mqAdminExt.shutdown();
                    return;
                }

                // 4、过滤与配置不相同的topic(topic对应的读写队列数量是否相同)
                needCreateOrUpdateTopicConfigInfos.addAll(topicConfigInfos.stream().filter(topicConfigInfo -> {
                    if (topicConfigInfo.getReadQueueNums() != mqProperties.getReadQueueNums() || topicConfigInfo.getWriteQueueNums() != mqProperties.getWriteQueueNums()) {
                        topicConfigInfo.setReadQueueNums(mqProperties.getReadQueueNums());
                        topicConfigInfo.setWriteQueueNums(mqProperties.getWriteQueueNums());
                        topicConfigInfo.setPerm(mqProperties.getPerm());
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList()));

            } else {
                TopicConfigInfo topicConfigInfo = new TopicConfigInfo();
                // 随机设置选取broker，以便同自动创建的队列保持一致（仅在其中一个broker上创建topic）
                List<String> brokerNameList = RocketMqAdmin.changeToBrokerNameList(clusterInfo, mqProperties);
                Random rand = SecureRandom.getInstanceStrong();
                int index = rand.nextInt(brokerNameList.size());
                topicConfigInfo.setBrokerNameList(Lists.newArrayList(brokerNameList.get(index)));
                topicConfigInfo.setTopicName(topic);
                topicConfigInfo.setWriteQueueNums(mqProperties.getWriteQueueNums());
                topicConfigInfo.setReadQueueNums(mqProperties.getReadQueueNums());
                topicConfigInfo.setPerm(mqProperties.getPerm());

                needCreateOrUpdateTopicConfigInfos.add(topicConfigInfo);
            }

            if (needCreateOrUpdateTopicConfigInfos.isEmpty()) {
                TOPIC_IS_READY.add(cacheKey);
            } else {
                for (TopicConfigInfo needCreateOrUpdateTopicConfigInfo : Optional.ofNullable(needCreateOrUpdateTopicConfigInfos).orElse(Collections.emptyList())) {
                    this.createAndUpdateTopic(cacheKey, topic, mqAdminExt, clusterInfo, needCreateOrUpdateTopicConfigInfo);
                }
            }
            mqAdminExt.shutdown();
        }
    }

    private void createAndUpdateTopic(String cacheKey, String topic, DefaultMQAdminExt mqAdminExt, ClusterInfo clusterInfo, TopicConfigInfo needCreateOrUpdateTopicConfigInfo) {
        TopicConfig topicConfig = new TopicConfig();
        BeanUtils.copyProperties(needCreateOrUpdateTopicConfigInfo, topicConfig);
        try {
            mqAdminExt.createAndUpdateTopicConfig(clusterInfo.getBrokerAddrTable().get(needCreateOrUpdateTopicConfigInfo.getBrokerNameList().get(0)).selectBrokerAddr(), topicConfig);

            // 等待topic创建完成(每隔0.1秒检测一次，总共等待1秒钟)
            boolean createResult = false;
            long startTime = System.currentTimeMillis();
            while (!createResult) {
                createResult = RocketMqAdmin.checkTopicExist(mqAdminExt, topic);
                if (System.currentTimeMillis() - startTime < mqProperties.getCreateTopicWaitTime() * 1000) {
                    RocketMqAdmin.waitForMoment(100);
                } else {
                    return;
                }
            }
            if (createResult) {
                TOPIC_IS_READY.add(cacheKey);
            }
        } catch (Exception e) {
            log.info("RocketMqProducer.createAndUpdateTopic,exception,topic={},e={}", topic, e.getMessage());
        }
    }

    private DefaultMQAdminExt getAndStartMQAdminExt(String topic) {
        DefaultMQAdminExt mqAdminExt = new DefaultMQAdminExt();
        mqAdminExt.setNamesrvAddr(producer.getNamesrvAddr());
        try {
            mqAdminExt.start();
        } catch (MQClientException e) {
            log.info("RocketMqProducer.getAndStartMQAdminExt,exception,topic={},e={}", topic, e.getMessage());
            return null;
        }
        return mqAdminExt;
    }
}
