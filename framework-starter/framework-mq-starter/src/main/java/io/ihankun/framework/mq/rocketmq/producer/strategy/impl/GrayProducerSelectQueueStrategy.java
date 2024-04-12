package io.ihankun.framework.mq.rocketmq.producer.strategy.impl;

import io.ihankun.framework.mq.rocketmq.producer.strategy.AbstractProducerSelectQueueStrategy;
import org.apache.rocketmq.client.impl.producer.TopicPublishInfo;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author hankun
 */
public class GrayProducerSelectQueueStrategy extends AbstractProducerSelectQueueStrategy {
    private int graySize;

    public GrayProducerSelectQueueStrategy(int graySize) {
        this.graySize = graySize;
    }

    @Override
    public MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName) {
        // 灰度消息发送到灰度队列
        List<MessageQueue> grayMessageQueueList =  tpInfo.getMessageQueueList().subList(0, graySize);
        return getMessageQueue(tpInfo, lastBrokerName, grayMessageQueueList);
    }
}
