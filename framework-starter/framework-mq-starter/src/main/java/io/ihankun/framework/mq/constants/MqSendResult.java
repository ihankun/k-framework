package io.ihankun.framework.mq.constants;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class MqSendResult {

    /**
     * 自定义的消息 ID
     */
    private String[] messageId;

    /**
     * RocketMQ 实际的消息 ID
     */
    private String[] mqMessageId;
}
