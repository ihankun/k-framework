package io.ihankun.framework.mq.producer.impl;

import io.ihankun.framework.common.v1.context.DomainContext;
import io.ihankun.framework.common.v1.id.IdGenerator;
import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.constants.MqSendResult;
import io.ihankun.framework.mq.message.MqMsg;
import io.ihankun.framework.mq.producer.IMqProducer;
import io.ihankun.framework.mq.producer.MqSendCallback;
import io.ihankun.framework.mq.producer.MqTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hankun
 */
@Slf4j
public class KafkaMqProducer implements IMqProducer {
    KafkaProducer<String, String> producer;

    private MqProperties properties;

    @Override
    public void init(MqProperties config) {

        if (config == null) {
            log.info("KafkaMqProducer.init.start,mq.producer.config.not.found,will not start");
            return;
        }

        //producer不启动
        if (config.getProducer() == null || !config.getConsumer().isEnable()) {
            log.info("KafkaMqProducer.init.finish,producer.start.not");
            return;
        }

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getUrl());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(properties);
    }

    @Override
    public <T> MqSendResult sendMsg(MqTopic topic, T data) {

        assertMessage(topic, data);

        MqMsg msg = buildMsg(topic, data);

        log.info("KafkaMqProducer.sendBatchMsg,topic={},msg={}", topic, msg);

        Future<RecordMetadata> send = producer.send(new ProducerRecord<>(topic.getTopic(),
                msg.serialize()));

        MqSendResult result = new MqSendResult();
        try {
            send.get();
            result.setMessageId(new String[]{msg.getMessageId()});
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public <T> MqSendResult sendBatchMsg(MqTopic topic, List<T> data) {

        assertMessage(topic, data);

        log.info("KafkaMqProducer.sendBatchMsg,topic={},msg={}", topic, data);

        MqSendResult result = new MqSendResult();
        List<String> messageId = new ArrayList<>(data.size());
        for (T item : data) {
            MqMsg msg = buildMsg(topic, item);

            producer.send(new ProducerRecord<>(topic.getTopic(), msg.serialize()));
            messageId.add(msg.getMessageId());

        }

        result.setMessageId(messageId.toArray(new String[0]));
        return result;
    }

    @Override
    public <T> void sendAsyncMsg(MqTopic topic, T data, MqSendCallback callback) {

        assertMessage(topic, data);

        MqMsg msg = buildMsg(topic, data);

        log.info("KafkaMqProducer.sendAsyncMsg,topic={},msg={}", topic, msg);

        producer.send(new ProducerRecord<>(topic.getTopic(), msg.serialize()), (metadata, exception) -> {

            if (callback == null) {
                log.info("KafkaMqProducer.sendAsyncMsg.ignore,callback is null");
                return;
            }

            if (exception != null) {
                callback.exception(exception);
                return;
            }

            MqSendResult result = new MqSendResult();
            result.setMessageId(new String[]{msg.getMessageId()});
            callback.success(result);

        });


    }

    @Override
    public <T> void sendAsyncBatchMsg(MqTopic topic, List<T> data, MqSendCallback callback) {

        assertMessage(topic, data);
        log.info("KafkaMqProducer.sendAsyncBatchMsg,topic={},data={}", topic, data);

        AtomicInteger sum = new AtomicInteger(data.size());
        List<String> messageId = new ArrayList<>(data.size());
        for (T item : data) {
            MqMsg msg = buildMsg(topic, item);
            producer.send(new ProducerRecord<>(topic.getTopic(),
                    msg.serialize()), new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    int num = sum.decrementAndGet();
                    if (num > 0) {
                        return;
                    }
                    //最后一个触发回调
                    if (callback == null) {
                        log.info("KafkaMqProducer.onCompletion.callback.null");
                        return;
                    }
                    if (exception != null) {
                        callback.exception(exception);
                        return;
                    }
                    MqSendResult result = new MqSendResult();
                    result.setMessageId(messageId.toArray(new String[0]));
                    callback.success(result);
                }
            });
            messageId.add(msg.getMessageId());
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
    }

    private <T> MqMsg buildMsg(MqTopic topic, T data) {
        try {

            MqMsg msg = new MqMsg();
            msg.setData(data);
            msg.setTopic(topic.getTopic());
            msg.setTag(topic.getTags());

            String domain = DomainContext.get();
            if (StringUtils.isEmpty(domain)) {
                log.error("RocketMqProducer.buildMsg.domain.null(构造MQ消息时未获取到当前登录信息的域名配置信息，请检查业务发送方逻辑)");
            }

            //设置domain，用于Sass化隔离
            msg.setTelnet(domain);

            String msgId = IdGenerator.ins().generator().toString();
            msg.setMessageId(msgId);

            return msg;

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public MqProperties properties() {
        return properties;
    }

    @Override
    public void start(MqProperties config) {
        if (config == null) {
            log.info("KafkaMqProducer.init.start,mq.producer.config.not.found,will not start");
            return;
        }

        //producer不启动
        if (config.getProducer() == null || !config.getConsumer().isEnable()) {
            log.info("KafkaMqProducer.init.finish,producer.start.not");
            return;
        }

        this.properties = config;

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getUrl());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(properties);
    }

    @Override
    public void restart(String url) {

    }

    @Override
    public void shutdown() {

    }
}
