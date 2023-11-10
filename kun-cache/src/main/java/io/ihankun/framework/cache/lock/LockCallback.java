package io.ihankun.framework.cache.lock;


import io.ihankun.framework.cache.error.CacheErrorCode;
import io.ihankun.framework.common.exception.BusinessException;

/**
 * @author hankun
 */
public interface LockCallback<T> {

    /**
     * 加锁成功后执行方法
     */
    T success();

    /**
     * 默认加锁失败时执行方法。
     */
    default T fail() {
        throw BusinessException.build(CacheErrorCode.NOT_FOUND_REDISSON ,RedissonLock.getCurrentLocKey());
    }
}
