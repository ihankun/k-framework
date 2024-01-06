package io.ihankun.framework.cache.lock;

import io.ihankun.framework.cache.error.RedissonLockErrorCode;
import io.ihankun.framework.cache.holder.RedissonClientHolder;
import io.ihankun.framework.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
@Component
public class RedissonLock {

    @PostConstruct
    public void init() {
        log.info("init RedissonLock");
    }

    /**
     * 存储当前线程的分布式锁key，用于异常下打印key
     */
    private static final ThreadLocal<String> LOC_KEY_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 分布式锁的前缀
     */
    private static final String LOCK_KEY_PREFIX = "distribute-lock:";

    /**
     * 获取分布式锁最大等待时间（秒）
     */
    public static final long LOCK_MAX_WAIT_SECTOND = 30;

    /**
     * 获取锁后多久自动释放锁（秒）
     */
    public static final long LOCK_MAX_LEASE_SECTOND = 60;

    /**
     * Redisson客户端
     */
    @Resource
    private RedissonClient redissonClient;


    /**
     * 获取分布式锁
     *
     * @param key      分布式锁key
     * @param callback 获取锁后的回调
     * @return 回调接口的返回值
     */
    public <T> T lock(String key, LockCallback<T> callback) {
        return lock(key, LOCK_MAX_WAIT_SECTOND, LOCK_MAX_LEASE_SECTOND, TimeUnit.SECONDS, callback);
    }

    /**
     * 获取分布式锁
     *
     * @param key       分布式锁key
     * @param waitTime  获取锁时最长等待时间
     * @param leaseTime 获取到锁后，多久后自动释放锁
     * @param timeUnit  时间单位
     * @param callback  回调操作
     * @return 回调接口的返回值
     */
    public <T> T lock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) {
        String locKey = LOCK_KEY_PREFIX + key;
        LOC_KEY_THREAD_LOCAL.set(locKey);
        RLock rLock = RedissonClientHolder.ins().getRedissonClient().getLock(locKey);
        try {
            boolean locked = rLock.tryLock(waitTime, leaseTime, timeUnit);

            //加锁成功执行成功回调逻辑
            if (locked) {
                return callback.success();
            }

            //加锁失败执行失败回调逻辑
            return callback.fail();
        } catch (InterruptedException e) {
            log.error("获取分布式锁时出现异常，lockKey = {},errorMsg = {}, error stack = {}", locKey, e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw BusinessException.build(RedissonLockErrorCode.GET_REDISSON_EX, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
            LOC_KEY_THREAD_LOCAL.remove();
        }
    }

    public static String get() {
        return LOC_KEY_THREAD_LOCAL.get();
    }
}
