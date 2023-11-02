package io.ihankun.framework.mq.producer;


import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.constants.MqSendResult;
import io.ihankun.framework.mq.nacos.IMqConfigChange;

import java.util.List;

/**
 * @author hankun
 */
public interface IMqProducer extends IMqConfigChange {

    /**
     * 初始化生产者
     *
     * @param config mq配置
     */
    void init(MqProperties config);


    /**
     * 同步发送单个消息
     *
     * @param topic 使用构造者构建topic与tag
     * @param data  待发送数据
     * @return
     */
    <T> MqSendResult sendMsg(MqTopic topic, T data);

    /**
     * 同步批量发送消息
     *
     * @param topic 使用构造者构建topic与tag
     * @param data  待发送数据
     * @return
     */
    <T> MqSendResult sendBatchMsg(MqTopic topic, List<T> data);


    /************************** Async Message  ***************************/

    /**
     * 异步发送单个消息
     *
     * @param topic    使用构造者构建topic与tag
     * @param data     待发送数据
     * @param callback 回调方法
     */
    <T> void sendAsyncMsg(MqTopic topic, T data, MqSendCallback callback);

    /**
     * 异步批量发送消息
     *
     * @param topic    使用构造者构建topic与tag
     * @param data     待发送数据
     * @param callback 回调方法
     */
    <T> void sendAsyncBatchMsg(MqTopic topic, List<T> data, MqSendCallback callback);
}
