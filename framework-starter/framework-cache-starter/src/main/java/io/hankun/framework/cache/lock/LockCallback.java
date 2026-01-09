package io.hankun.framework.cache.lock;

import io.hankun.framework.cache.error.RedissonLockErrorCode;
import io.hankun.framework.core.exception.BusinessException;

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
        throw BusinessException.build(RedissonLockErrorCode.NOT_FOUND_REDISSON , RedissonLock.get());
    }
}
