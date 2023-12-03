package io.ihankun.framework.common.http.v1;

/**
 * @author hankun
 */
public interface RemoteCallBack<T> {

    /**
     * 成功后的回调逻辑
     *
     * @param data responseResult中的数据
     */
    default void onSuccess(T data) {
    }

    /**
     * 失败后的回调逻辑
     *
     * @param t 异常信息
     */
    default void onFailure(Throwable t) {
    }
}
