package io.hankun.framework.cache.lock;

/**
 * @author hankun
 */
public interface LockKey {

    /**
     * 获取加锁的key
     */
    String getLockKey();
}
