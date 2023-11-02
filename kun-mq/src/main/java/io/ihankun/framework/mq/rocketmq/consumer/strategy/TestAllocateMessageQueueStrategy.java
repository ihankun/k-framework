package io.ihankun.framework.mq.rocketmq.consumer.strategy;

import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.constants.EnvMark;
import io.ihankun.framework.mq.rocketmq.producer.MqProducer;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.logging.InternalLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 重写消费端注册进行rebalance时选择queue的逻辑，
 * 仅限测试环境有效
 *
 * @author xiangguangdong 2022-06-23
 */
public class TestAllocateMessageQueueStrategy implements AllocateMessageQueueStrategy {
    private final InternalLogger log = ClientLogger.getLog();

    private MqProperties mqProperties;

    public TestAllocateMessageQueueStrategy(MqProperties mqProperties) {
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
        int queueSize = mqAll.size();
        int graySize = mqProperties.getGraySize();

        //明明符合规范，则进入匹配规则
        if (currentCID.contains(MqProducer.CLIENT_ID_SPLIT)) {
            String mark = currentCID.split(MqProducer.CLIENT_ID_SPLIT)[0];

            //如果所有cid列表不包括当前cid，则说明有bug，返回空数组
            if (!cidAll.contains(currentCID)) {
                log.error("AllocateMessageQueueStrategy.allocate.BUG,consumerGroup={},currentCID={},selectedCidAll={}", consumerGroup, currentCID, cidAll);
                return null;
            }


            String cidStatus = cidExits(cidAll);

            switch (cidStatus) {
                //正式、灰度、灰灰度都存在，每个消费均独立不同的queue
                case "111": {
                    if (EnvMark.PROD.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(graySize, queueSize - graySize);
                    } else if (EnvMark.GRAY.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(0, graySize);
                    } else {
                        selectedMqAll = mqAll.subList(queueSize - graySize, queueSize);
                    }
                }
                break;
                //只有正式节点
                case "100": {
                    if (EnvMark.PROD.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(0, queueSize);
                    }
                }
                break;
                //只有灰度节点
                case "010": {
                    if (EnvMark.GRAY.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(0, queueSize);
                    }
                }
                //只有灰灰度节点
                case "001": {
                    if (EnvMark.GGRAY.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(queueSize - graySize, queueSize);
                    }
                }
                break;
                //正式+灰度
                case "110": {
                    if (EnvMark.PROD.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(graySize, queueSize);
                    } else if (EnvMark.GRAY.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(0, graySize);
                    }
                }
                break;
                //正式+灰灰度
                case "101": {
                    if (EnvMark.PROD.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(0, queueSize - graySize);
                    } else if (EnvMark.GGRAY.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(queueSize - graySize, queueSize);
                    }
                }
                break;
                //灰度+灰灰度
                case "011": {
                    if (EnvMark.GRAY.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(0, queueSize - graySize);
                    } else if (EnvMark.GGRAY.getEnv().equals(mark)) {
                        selectedMqAll = mqAll.subList(queueSize - graySize, queueSize);
                    }
                }
                break;
            }

            List<String> selectedCidAll = cidAll.stream().filter(cid -> cid.startsWith(mark)).collect(Collectors.toList());

            return selectQueue(currentCID, selectedCidAll, selectedMqAll);
        }

        return selectQueue(currentCID, cidAll, selectedMqAll);
    }

    /**
     * 选择队列
     *
     * @param currentCID
     * @param selectedCidAll
     * @param selectedMqAll
     * @return
     */
    private List<MessageQueue> selectQueue(String currentCID, List<String> selectedCidAll, List<MessageQueue> selectedMqAll) {
        if (selectedMqAll == null) {
            return null;
        }
        List<MessageQueue> result = new ArrayList<>();
        int index = selectedCidAll.indexOf(currentCID);
        int mod = selectedMqAll.size() % selectedCidAll.size();
        int averageSize = selectedMqAll.size() <= selectedCidAll.size() ? 1 : (mod > 0 && index < mod ? selectedMqAll.size() / selectedCidAll.size() + 1 : selectedMqAll.size() / selectedCidAll.size());
        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod;
        int range = Math.min(averageSize, selectedMqAll.size() - startIndex);
        for (int i = 0; i < range; i++) {
            result.add(selectedMqAll.get((startIndex + i) % selectedMqAll.size()));
        }
        return result;
    }

    @Override
    public String getName() {
        return "KUN-TEST";
    }

    /**
     * CID表示判断
     *
     * @param cidAll
     * @return
     */
    private String cidExits(List<String> cidAll) {
        String[] flag = {"0", "0", "0"};
        for (String cid : cidAll) {
            if (cid.startsWith(EnvMark.PROD.getEnv())) {
                flag[0] = "1";
            }
            if (cid.startsWith(EnvMark.GRAY.getEnv())) {
                flag[1] = "1";
            }
            if (cid.startsWith(EnvMark.GGRAY.getEnv())) {
                flag[2] = "1";
            }
        }
        return String.join("", flag);
    }
}
