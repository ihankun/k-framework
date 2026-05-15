package io.hankun.framework.cache.holder;

import io.hankun.framework.core.utils.spring.SpringHelpers;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author hankun
 */
@Slf4j
public class RedissonClientHolder {

    private static final AtomicReference<RedissonClientHolder> INSTANCE = new AtomicReference<>();

    private final AtomicReference<RedissonClient> redissonClient = new AtomicReference<>();

    private RedissonClientHolder() {}

    public static RedissonClientHolder ins() {
        RedissonClientHolder currentInstance = INSTANCE.get();
        if (currentInstance == null) {
            synchronized (RedissonClientHolder.class) {
                currentInstance = INSTANCE.get();
                if (currentInstance == null) {
                    currentInstance = new RedissonClientHolder();
                    INSTANCE.set(currentInstance);
                }
            }
        }
        return currentInstance;
    }

    public RedissonClient getRedissonClient() {
        RedissonClient localInstance = redissonClient.get();
        if (localInstance == null) {
            synchronized (RedissonClientHolder.class) {
                localInstance = redissonClient.get();
                if (localInstance == null) {
                    RedissonClient client = SpringHelpers.context().getBean(RedissonClient.class);
                    redissonClient.set(client);
                    localInstance = client;
                }
            }
        }
        return localInstance;
    }

    public static void setRedissonClient(RedissonClient redissonClient) {
        ins().setRedissonClientInternal(redissonClient);
    }

    private synchronized void setRedissonClientInternal(RedissonClient redissonClient) {
        this.redissonClient.set(redissonClient);
    }
}
