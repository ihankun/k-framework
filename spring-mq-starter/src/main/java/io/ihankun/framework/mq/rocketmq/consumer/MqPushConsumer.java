package io.ihankun.framework.mq.rocketmq.consumer;

import io.ihankun.framework.common.utils.ip.IpUtil;
import io.ihankun.framework.mq.constants.EnvMark;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.remoting.RPCHook;

/**
 * @author hankun
 */
@Slf4j
public class MqPushConsumer extends DefaultMQPushConsumer {

    private static final String CLIENT_ID_SPLIT = "@";
    private String grayMark;

    public MqPushConsumer(final String consumerGroup, RPCHook rpcHook,
                          AllocateMessageQueueStrategy allocateMessageQueueStrategy, String grayMark) {
        super(consumerGroup, rpcHook, allocateMessageQueueStrategy);
        this.grayMark = grayMark;
    }

    /**
     * 重写消费者ClientId的生成方式，用于区分生产和灰度节点的消费者
     * @return
     */
    @Override
    public String buildMQClientId() {
        StringBuilder sb = new StringBuilder();
        if (Boolean.FALSE.toString().equalsIgnoreCase(grayMark)) {
            // 生产
            sb.append(EnvMark.PROD.getEnv());
        } else if (Boolean.TRUE.toString().equalsIgnoreCase(grayMark)) {
            // 灰度
            sb.append(EnvMark.GRAY.getEnv());
        }
        sb.append(CLIENT_ID_SPLIT);
        // sb.append(this.getClientIP());
        sb.append(IpUtil.getIp());
        sb.append(CLIENT_ID_SPLIT);
        sb.append(this.getInstanceName());
        if (!UtilAll.isBlank(this.getUnitName())) {
            sb.append(CLIENT_ID_SPLIT);
            sb.append(this.getUnitName());
        }
        log.info("clientId:{}",sb.toString());
        return sb.toString();
    }
}
