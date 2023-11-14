package io.ihankun.framework.mq.consumer;


import io.ihankun.framework.mq.message.ReceiveMessage;

import java.util.List;

/**
 * @author hankun
 */
public interface ConsumerListener<T> {

    /**
     * 配置
     *
     * @return
     */
    default ConsumerListenerConfig config() {
        return ConsumerListenerConfig.builder().idempotent(true).build();
    }

    /**
     * 订阅对象
     *
     * @return
     */
    String subscribeTopic();


    /**
     * 订阅tags
     *
     * @return
     */
    String subscribeTags();

    /**
     * 推送消息回调
     *
     * @param list
     * @return
     */
    boolean receive(List<ReceiveMessage<T>> list);
}
