package io.ihankun.framework.cache.nats.annotation;

import io.nats.client.Consumer;

import java.lang.annotation.*;

/**
 * nats stream 监听器注解
 *
 * @author hankun
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NatsStreamListener {

    /**
     * 主题 subject
     *
     * @return subject
     */
    String value();

    /**
     * 队列
     *
     * @return 队列名称
     */
    String queue() default "";

    /**
     * Stream（消息流）
     *
     * @return Stream name
     */
    String stream() default "";

    /**
     * 自动 ack
     *
     * @return 是否自动 ack
     */
    boolean autoAck() default false;

    /**
     * 是否按顺序消费
     *
     * @return 顺序消费
     */
    boolean ordered() default false;

    /**
     * 设置非调度推送订阅在内部（挂起）消息队列中所能容纳的最大消息数量。
     *
     * @return 最大消息数量
     */
    long pendingMessageLimit() default Consumer.DEFAULT_MAX_MESSAGES;

    /**
     * 设置非调度推送订阅在内部（挂起）消息队列中所能容纳的最大字节数。
     *
     * @return 最大字节数
     */
    long pendingByteLimit() default Consumer.DEFAULT_MAX_BYTES;

}
