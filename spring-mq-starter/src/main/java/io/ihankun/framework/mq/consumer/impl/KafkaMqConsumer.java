package io.ihankun.framework.mq.consumer.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import io.ihankun.framework.common.v1.thread.NamedThreadFactory;
import io.ihankun.framework.common.v1.utils.spring.SpringHelpers;
import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.consumer.AbstractConsumer;
import io.ihankun.framework.mq.consumer.ConsumerListener;
import io.ihankun.framework.mq.consumer.ConsumerListenerConfig;
import io.ihankun.framework.mq.consumer.IMqConsumer;
import io.ihankun.framework.mq.message.MqMsg;
import io.ihankun.framework.mq.message.ReceiveMessage;
import io.ihankun.framework.mq.producer.MqTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

/**
 * @author hankun
 */
@Component("kafkaConsumer")
@Slf4j
public class KafkaMqConsumer extends AbstractConsumer implements IMqConsumer {

    private MqProperties properties;


    @Override
    public MqProperties properties() {
        return properties;
    }

    @Override
    public void restart(String url) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void start(MqProperties config) {

        if (config == null) {
            log.info("KafkaMqConsumer.init.start,config.null");
            return;
        }

        this.properties = config;

        if (config.getConsumer() == null || !config.getConsumer().isEnable()) {
            log.info("KafkaMqConsumer.init.stop,enable=false");
            return;
        }

        Map<String, ConsumerListener> beans = SpringHelpers.context().getBeansOfType(ConsumerListener.class);

        if (MapUtil.isEmpty(beans)) {
            return;
        }


        for (ConsumerListener listener : beans.values()) {
            buildConsumerTask(config, listener);
            log.info("KafkaMqConsumer.init.build.task,consumer={}", listener.getClass().getSimpleName());
        }

    }


    private void buildConsumerTask(MqProperties config, ConsumerListener listener) {

        //获取服务名，同一个服务作为一个消费组概念
        Environment environment = SpringHelpers.context().getEnvironment();
        String consumerGroupName = environment.getProperty("spring.application.name", "default_consumer_group");

        //构造consumer
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getUrl());
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupName + "-" + listener.getClass().getSimpleName());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        String topic = listener.subscribeTopic();
        consumer.subscribe(Arrays.asList(topic));

        //构造循环线程池
        NamedThreadFactory threadFactory = new NamedThreadFactory("kafka-consumer-" + listener.getClass().getSimpleName());
        Thread thread = threadFactory.newThread(() -> {
            while (true) {
                try {
                    fetch(consumer, topic, listener);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
        log.info("KafkaMqConsumer.buildConsumerTask.start,threadName={}", thread.getName());
        thread.start();
    }

    private void fetch(KafkaConsumer consumer, String topic, ConsumerListener listener) {
        //获取100ms之内的数据
        ConsumerRecords<String, String> poll = consumer.poll(Duration.ofMillis(100));
        if (!poll.isEmpty()) {
            log.info("KafkaMqConsumer.fetch.success,data.size={}", poll.count());
            //获取订阅记录
            //组装messageList

            Iterable<ConsumerRecord<String, String>> records = poll.records(topic);
            Iterator<ConsumerRecord<String, String>> iterator = records.iterator();

            List<ReceiveMessage> dataList = new ArrayList<>();

            while (iterator.hasNext()) {
                ConsumerRecord<String, String> next = iterator.next();
                String value = next.value();
                MqMsg msg = JSON.parseObject(value, MqMsg.class);

                ReceiveMessage receive = new ReceiveMessage();
                receive.setTopic(MqTopic.builder().topic(topic).build());
                receive.setMessageId(msg.getMessageId());

                receive.setData(objectToClass(msg.getData(), listener));

                ConsumerListenerConfig listenerConfig = listener.config();
                //如果开启幂等处理
                if (listenerConfig != null && listenerConfig.isIdempotent()) {

                    //如果未设置幂等key，则以消息id为key
                    String idempotentKey = getIdempotentKey(listenerConfig, msg.getMessageId(), msg.getData());

                    boolean beConsumed = this.checkConsumed(idempotentKey);
                    if (!beConsumed) {
                        this.confirmConsumed(idempotentKey);
                    }
                    receive.setBeConsumed(beConsumed);
                }

                receive.setBeConsumed(false);
                dataList.add(receive);
            }

            log.info("KafkaMqConsumer.fetch.success,data={}", dataList);
            //发送
            try {
                boolean receive = listener.receive(dataList);
                if (receive) {
                    //消费成功
                    consumer.commitSync();
                    return;
                }

                //消费失败，如果开启幂等，重置幂等
                if (listener.config() != null && listener.config().isIdempotent()) {
                    for (ReceiveMessage message : dataList) {
                        //如果未设置幂等key，则以消息id为key
                        String idempotentKey = getIdempotentKey(listener.config(), message.getMessageId(), message.getData());
                        resetConsumed(idempotentKey);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
    }
}
