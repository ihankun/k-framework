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
public class GrayStrategy extends MQFaultStrategy {

    private final static InternalLogger LOG = ClientLogger.getLog();
    private final LatencyFaultTolerance<String> latencyFaultTolerance = new LatencyFaultToleranceImpl();

    private long[] latencyMax = {50L, 100L, 550L, 1000L, 2000L, 3000L, 15000L};
    private long[] notAvailableDuration = {0L, 0L, 30000L, 60000L, 120000L, 180000L, 600000L};

    private int graySize = 1;

    public GrayStrategy(int graySize) {
        this.graySize = graySize;
    }

    @Override
    public MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName) {
        // 生产消息发送到除灰度消息之外的其他队列，截取graySize之外的队列
        List<MessageQueue> prodMessageQueueList =  tpInfo.getMessageQueueList().subList(0, graySize);
        if (isSendLatencyFaultEnable()) {
            try {
                int index = tpInfo.getSendWhichQueue().getAndIncrement();
                for (int i = 0; i < prodMessageQueueList.size(); i++) {
                    int pos = Math.abs(index++) % prodMessageQueueList.size();
                    if (pos < 0) {
                        pos = 0;
                    }
                    MessageQueue mq = prodMessageQueueList.get(pos);
                    if (latencyFaultTolerance.isAvailable(mq.getBrokerName())) {
                        if (null == lastBrokerName || mq.getBrokerName().equals(lastBrokerName)) {
                            return mq;
                        }
                    }
                }

                final String notBestBroker = latencyFaultTolerance.pickOneAtLeast();
                int writeQueueNums = tpInfo.getQueueIdByBroker(notBestBroker);
                if (writeQueueNums > 0) {
                    final MessageQueue mq = this.selectOneMessageQueueFromTopicInfo(tpInfo, lastBrokerName, prodMessageQueueList);
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

            return this.selectOneMessageQueueFromTopicInfo(tpInfo, lastBrokerName, prodMessageQueueList);
        }

        return this.selectOneMessageQueueFromTopicInfo(tpInfo, lastBrokerName, prodMessageQueueList);
    }

    private MessageQueue selectOneMessageQueueFromTopicInfo(TopicPublishInfo tpInfo, String lastBrokerName, List<MessageQueue> prodMessageQueueList) {
        if (lastBrokerName == null) {
            return this.selectOneMessageQueueFromTopicInfo(tpInfo, prodMessageQueueList);
        } else {
            int index = tpInfo.getSendWhichQueue().getAndIncrement();
            for (int i = 0; i < prodMessageQueueList.size(); i++) {
                int pos = Math.abs(index++) % prodMessageQueueList.size();
                if (pos < 0) {
                    pos = 0;
                }
                MessageQueue mq = prodMessageQueueList.get(pos);
                if (!mq.getBrokerName().equals(lastBrokerName)) {
                    return mq;
                }
            }
            return this.selectOneMessageQueueFromTopicInfo(tpInfo, prodMessageQueueList);
        }
    }

    public MessageQueue selectOneMessageQueueFromTopicInfo(TopicPublishInfo tpInfo, List<MessageQueue> prodMessageQueueList) {
        int index = tpInfo.getSendWhichQueue().getAndIncrement();
        int pos = Math.abs(index) % prodMessageQueueList.size();
        if (pos < 0) {
            pos = 0;
        }
        return prodMessageQueueList.get(pos);
    }

    @Override
    public void updateFaultItem(final String brokerName, final long currentLatency, boolean isolation) {
        if (isSendLatencyFaultEnable()) {
            long duration = computeNotAvailableDuration(isolation ? 30000 : currentLatency);
            this.latencyFaultTolerance.updateFaultItem(brokerName, currentLatency, duration);
        }
    }

    private long computeNotAvailableDuration(final long currentLatency) {
        for (int i = latencyMax.length - 1; i >= 0; i--) {
            if (currentLatency >= latencyMax[i]) {
                return this.notAvailableDuration[i];
            }
        }

        return 0;
    }
}
