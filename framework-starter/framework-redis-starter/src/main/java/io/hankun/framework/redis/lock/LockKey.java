package io.hankun.framework.redis.lock;

/**
 * @author hankun
 */
public interface LockKey {

    /**
     * 获取加锁的key
     */
    String getLockKey();
}
