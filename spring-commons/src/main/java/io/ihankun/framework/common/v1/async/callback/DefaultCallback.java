package io.ihankun.framework.common.v1.async.callback;


import io.ihankun.framework.common.v1.async.worker.WorkResult;

/**
 * 默认回调类，如果不设置的话，会默认给这个回调
 * @author hankun wrote on 2019-11-19.
 */
public class DefaultCallback<T, V> implements ICallback<T, V> {
    @Override
    public void begin() {

    }

    @Override
    public void result(boolean success, T param, WorkResult<V> workResult) {

    }

}
