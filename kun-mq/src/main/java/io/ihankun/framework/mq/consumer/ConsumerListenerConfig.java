package io.ihankun.framework.mq.consumer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hankun
 */
@Getter
@Builder
public class ConsumerListenerConfig {

    /**
     * 消费组名，默认为当前微服务名
     */
    @Setter
    private String consumerGroupName;

    /**
     * 是否为广播模式，默认为false【集群模式】
     */
    @Builder.Default
    private boolean boardCast = false;

    /**
     * 是否开启幂等判断开关，默认false【关闭】
     */
    @Setter
    @Builder.Default
    @Deprecated
    private boolean idempotent = false;

    /**
     * 自定义幂等key，若不设置，则默认使用msgId作为key
     * 默认【null】
     */
    @Deprecated
    private String idempotentKey;
}
