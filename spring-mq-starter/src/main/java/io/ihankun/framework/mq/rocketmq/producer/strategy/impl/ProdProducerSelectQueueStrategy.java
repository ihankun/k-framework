package io.ihankun.framework.mq.rocketmq.producer.strategy.impl;

import io.ihankun.framework.mq.rocketmq.producer.strategy.AbstractProducerSelectQueueStrategy;
import org.apache.rocketmq.client.impl.producer.TopicPublishInfo;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * @author hankun
 */
public class ProdProducerSelectQueueStrategy extends AbstractProducerSelectQueueStrategy {
    private int graySize;

    public ProdProducerSelectQueueStrategy(int graySize) {
        this.graySize = graySize;
    }

    @Override
    public MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName) {
        // 生产消息发送到生产队列
        int queueSize = tpInfo.getMessageQueueList().size();
        List<MessageQueue> prodMessageQueueList = tpInfo.getMessageQueueList().subList(graySize, queueSize - graySize);
        return getMessageQueue(tpInfo, lastBrokerName, prodMessageQueueList);
    }
}
