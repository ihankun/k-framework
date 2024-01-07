package io.ihankun.framework.mongoplus.handlers;

/**
 * @author hankun
 */
public interface Executor<T> {

    /**
     * 执行
     */
    void invoke(T cModel) throws Exception;
}
