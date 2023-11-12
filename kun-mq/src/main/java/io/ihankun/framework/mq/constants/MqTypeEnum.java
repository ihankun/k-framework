package io.ihankun.framework.mq.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@AllArgsConstructor
@Getter
public enum MqTypeEnum {

    /**
     * rocketMq
     */
    ROCKETMQ("rocketmq", "RocketMQ"),
    /**
     * kafka
     */
    KAFKA("kafka", "Kafka");

    private String type;
    private String description;
}
