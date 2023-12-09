package io.ihankun.framework.mongo.handler;

/**
 * @author hankun
 */
public interface Executor<T> {

    /**
     * 执行
     */
    void invoke(T cModel) throws Exception;
}
