package io.ihankun.framework.cache.holder;


import io.ihankun.framework.common.utils.SpringHelpers;
import org.redisson.api.RedissonClient;

/**
 * @author hankun
 */
public class RedissonClientHolder {
    private static class RedissonClientHolderInstance {
        private static final RedissonClientHolder INSTANCE = new RedissonClientHolder();
    }

    private volatile RedissonClient redissonClient;
    private RedissonClientHolder() {}

    public static RedissonClientHolder ins() {
        return RedissonClientHolderInstance.INSTANCE;
    }

    public RedissonClient getRedissonClient() {
        if (redissonClient == null) {
            synchronized (RedissonClientHolder.class) {
                if (redissonClient == null) {
                    redissonClient = SpringHelpers.context().getBean(RedissonClient.class);
                }
            }
        }
        return redissonClient;
    }

    public static void setRedissonClient(RedissonClient redissonClient) {
        RedissonClientHolder.ins().redissonClient = redissonClient;
    }
}
