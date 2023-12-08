package io.ihankun.framework.cache.lock.annotation;


import io.ihankun.framework.cache.lock.RedissonLock;

import java.lang.annotation.*;

/**
 * @author hankun
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

    /**
     * 加锁失败后的默认提示信息
     */
    public static final String MESSAGE = "系统繁忙,请稍候再试!";

    /**
     * 加锁失败的提示信息
     *
     * @return
     */
    String value() default MESSAGE;

    /**
     * 获取分布式锁最大等待时间（秒）
     *
     * @return
     */
    long waitTime() default RedissonLock.LOCK_MAX_WAIT_SECTOND;

    /**
     * 获取锁后多久自动释放锁（秒）
     *
     * @return
     */
    long leaseTime() default RedissonLock.LOCK_MAX_LEASE_SECTOND;
}
