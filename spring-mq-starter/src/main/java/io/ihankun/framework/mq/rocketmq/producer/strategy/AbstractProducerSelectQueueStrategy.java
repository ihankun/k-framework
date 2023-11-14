package io.ihankun.framework.mq.rocketmq.producer.strategy;

import org.apache.rocketmq.client.impl.producer.TopicPublishInfo;
import org.apache.rocketmq.client.latency.LatencyFaultTolerance;
import org.apache.rocketmq.client.latency.LatencyFaultToleranceImpl;
import org.apache.rocketmq.client.latency.MQFaultStrategy;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.logging.InternalLogger;

import java.util.List;

/**
 * @author hankun
 */
public abstract class AbstractProducerSelectQueueStrategy extends MQFaultStrategy {

    private final static InternalLogger LOG = ClientLogger.getLog();

    private final LatencyFaultTolerance<String> latencyFaultTolerance = new LatencyFaultToleranceImpl();

    /**
     * 生产者队列选择逻辑抽象方法
     * @param tpInfo
     * @param lastBrokerName
     * @return
     */
    @Override
    public abstract MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName);

    /**
     * 根据不通环境的队列进行负载均衡计算
     * @param tpInfo
     * @param lastBrokerName
     * @param messageQueueList
     * @return
     */
    protected MessageQueue getMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName, List<MessageQueue> messageQueueList) {
        if (isSendLatencyFaultEnable()) {
            try {
                int index = tpInfo.getSendWhichQueue().getAndIncrement();
                for (int i = 0; i < messageQueueList.size(); i++) {
                    int pos = Math.abs(index++) % messageQueueList.size();
                    if (pos < 0) {
                        pos = 0;
                    }
                    MessageQueue mq = messageQueueList.get(pos);
                    if (latencyFaultTolerance.isAvailable(mq.getBrokerName())) {
                        if (null == lastBrokerName || mq.getBrokerName().equals(lastBrokerName)) {
                            return mq;
                        }
                    }
                }

                final String notBestBroker = latencyFaultTolerance.pickOneAtLeast();
                int writeQueueNums = tpInfo.getQueueIdByBroker(notBestBroker);
                if (writeQueueNums > 0) {
                    final MessageQueue mq = this.selectOneMessageQueueFromTopicInfo(tpInfo, lastBrokerName, messageQueueList);
                    if (notBestBroker != null) {
                        mq.setBrokerName(notBestBroker);
                        mq.setQueueId(tpInfo.getSendWhichQueue().getAndIncrement() % writeQueueNums);
                    }
                    return mq;
                } else {
                    latencyFaultTolerance.remove(notBestBroker);
                }
            } catch (Exception e) {
                LOG.error("Error occurred when selecting message queue", e);
            }

            return this.selectOneMessageQueueFromTopicInfo(tpInfo, lastBrokerName, messageQueueList);
        }

        return this.selectOneMessageQueueFromTopicInfo(tpInfo, lastBrokerName, messageQueueList);
    }

    /**
     * 负载均衡逻辑
     * @param tpInfo
     * @param lastBrokerName
     * @param messageQueueList
     * @return
     */
    protected MessageQueue selectOneMessageQueueFromTopicInfo(TopicPublishInfo tpInfo, String lastBrokerName, List<MessageQueue> messageQueueList) {
        if (lastBrokerName == null) {
            return this.selectOneMessageQueueFromTopicInfo(tpInfo, messageQueueList);
        } else {
            int index = tpInfo.getSendWhichQueue().getAndIncrement();
            for (int i = 0; i < messageQueueList.size(); i++) {
                int pos = Math.abs(index++) % messageQueueList.size();
                if (pos < 0) {
                    pos = 0;
                }
                MessageQueue mq = messageQueueList.get(pos);
                if (!mq.getBrokerName().equals(lastBrokerName)) {
                    return mq;
                }
            }
            return this.selectOneMessageQueueFromTopicInfo(tpInfo, messageQueueList);
        }
    }

    /**
     * 负载均衡逻辑
     * @param tpInfo
     * @param messageQueueList
     * @return
     */
    protected MessageQueue selectOneMessageQueueFromTopicInfo(TopicPublishInfo tpInfo, List<MessageQueue> messageQueueList) {
        int index = tpInfo.getSendWhichQueue().getAndIncrement();
        int pos = Math.abs(index) % messageQueueList.size();
        if (pos < 0) {
            pos = 0;
        }
        return messageQueueList.get(pos);
    }
}
