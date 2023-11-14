package io.ihankun.framework.mq.rocketmq.consumer.strategy;

import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.constants.EnvMark;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.logging.InternalLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
public class AllocateMessageQueueStrategy implements org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy {

    private final InternalLogger log = ClientLogger.getLog();

    private MqProperties mqProperties;

    public AllocateMessageQueueStrategy(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll, List<String> cidAll) {
        if (currentCID == null || currentCID.length() < 1) {
            throw new IllegalArgumentException("currentCID is empty");
        }
        if (mqAll == null || mqAll.isEmpty()) {
            throw new IllegalArgumentException("mqAll is null or mqAll empty");
        }
        if (cidAll == null || cidAll.isEmpty()) {
            throw new IllegalArgumentException("cidAll is null or cidAll empty");
        }

        List<MessageQueue> selectedMqAll = mqAll;
        List<String> selectedCidAll = cidAll;
        int queueSize = mqAll.size();
        int graySize = mqProperties.getGraySize();
        if (currentCID.startsWith(EnvMark.GRAY.getEnv())) {
            // 判断是否有生产节点是否有标识，无生产标识说明生产消费者未上线，走原有逻辑。有标识的生产者说明生产逻辑已上线走新逻辑
            // 生产节点未上线灰度节点上线的场景下会发生灰度节点注册消费者到灰度队列不成功的场景
            selectedCidAll = cidAll.stream().filter(cid -> cid.startsWith(EnvMark.GRAY.getEnv())).collect(Collectors.toList());
            boolean isProdCidExisted = this.isProdCidExisted(cidAll);
            if (isProdCidExisted) {
                // 灰度消费节点消费灰度的消费队列
                selectedMqAll = mqAll.subList(0, graySize);
            }
        } else if (currentCID.startsWith(EnvMark.PROD.getEnv())) {
            // 生产消费节点首先判断是否有灰度消费节点，无灰度消费节点的场景消费灰度节点队列和非灰度节点队列
            // 1、判断当前topic是否存在其他的灰度消费者
            selectedCidAll = cidAll.stream().filter(cid -> cid.startsWith(EnvMark.PROD.getEnv())).collect(Collectors.toList());
            boolean isGrayCidExisted = this.isGrayCidExisted(cidAll);
            if (isGrayCidExisted) {
                // 有其他灰度消费者的场景下，当前非灰度消费节点仅消费非灰度的queue
                selectedMqAll = mqAll.subList(graySize, queueSize - graySize);
            }
            // 没有灰度消费者的场景下，当前生产消费节点也消费灰度的队列
        }

        List<MessageQueue> result = new ArrayList<MessageQueue>();
        if (!selectedCidAll.contains(currentCID)) {
            log.info("[BUG] ConsumerGroup: {} The consumerId: {} not in selectedCidAll: {}",
                    consumerGroup,
                    currentCID,
                    selectedCidAll);
            return result;
        }

        int index = selectedCidAll.indexOf(currentCID);
        int mod = selectedMqAll.size() % selectedCidAll.size();
        int averageSize =
                selectedMqAll.size() <= selectedCidAll.size() ? 1 : (mod > 0 && index < mod ? selectedMqAll.size() / selectedCidAll.size()
                        + 1 : selectedMqAll.size() / selectedCidAll.size());
        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod;
        int range = Math.min(averageSize, selectedMqAll.size() - startIndex);
        for (int i = 0; i < range; i++) {
            result.add(selectedMqAll.get((startIndex + i) % selectedMqAll.size()));
        }
        return result;
    }

    @Override
    public String getName() {
        return "KUN";
    }

    /**
     * 判断是否存在灰度消费节点
     * @return
     */
    private boolean isGrayCidExisted(List<String> cidAll) {
        boolean isGrayCidExisted = false;
        for (String cid : cidAll) {
            if (cid.startsWith(EnvMark.GRAY.getEnv())) {
                isGrayCidExisted = true;
                break;
            }
        }
        return isGrayCidExisted;
    }

    /**
     * 判断是否存在生产消费节点
     * @return
     */
    private boolean isProdCidExisted(List<String> cidAll) {
        boolean isProdCidExisted = false;
        for (String cid : cidAll) {
            if (cid.startsWith(EnvMark.PROD.getEnv())) {
                isProdCidExisted = true;
                break;
            }
        }
        return isProdCidExisted;
    }
}
