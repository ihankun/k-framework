package io.ihankun.framework.mq.producer;

import io.ihankun.framework.mq.constants.MqSendResult;

/**
 * @author hankun
 */
public interface MqSendCallback {

    /**
     * 成功回调
     *
     * @param result
     */
    void success(MqSendResult result);

    /**
     * 异常回调
     *
     * @param e
     */
    void exception(Throwable e);
}
