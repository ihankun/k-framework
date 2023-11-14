package io.ihankun.framework.mq.rocketmq.producer.strategy.impl;

import io.ihankun.framework.mq.rocketmq.producer.strategy.AbstractProducerSelectQueueStrategy;
import org.apache.rocketmq.client.impl.producer.TopicPublishInfo;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author hankun
 */
public class GgrayProducerSelectQueueStrategy extends AbstractProducerSelectQueueStrategy {

    private int graySize;

    public GgrayProducerSelectQueueStrategy(int graySize) {
        this.graySize = graySize;
    }

    @Override
    public MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName) {
        // 灰灰度消息发送到灰灰度队列
        int queueSize = tpInfo.getMessageQueueList().size();
        List<MessageQueue> ggrayMessageQueueList = tpInfo.getMessageQueueList().subList(queueSize - graySize, queueSize);
        return getMessageQueue(tpInfo, lastBrokerName, ggrayMessageQueueList);
    }
}
