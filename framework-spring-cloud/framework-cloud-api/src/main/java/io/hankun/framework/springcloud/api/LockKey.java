package io.hankun.framework.springcloud.api;

/**
 * @author hankun
 */
public interface LockKey {

    /**
     * 获取加锁的key
     */
    String getLockKey();
}
