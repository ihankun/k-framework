package io.ihankun.framework.mq.message;

import io.ihankun.framework.mq.producer.MqTopic;
import lombok.Data;

/**
 * @author hankun
 */
@Data
public class ReceiveMessage<T> {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 主题
     */
    private MqTopic topic;

    /**
     * 只有consumerListenerConfig中的idempotent开关开启时，此字段才会生效
     * 是否被消费了，用于幂等判断
     */
    private boolean beConsumed;

    /**
     * 接收的数据
     */
    private T data;
}
