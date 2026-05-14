package io.hankun.framework.ai.context.store;

import io.hankun.framework.ai.core.redis.KRedisHolder;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Map;

/**
 * @description:
 * @className: ContextStore
 * @createAt: 2025/10/17 16:20
 * @author: hankun
 */
public abstract class ContextStore {

    private final RedissonClient redissonClient;

    private final String prefix;

    public abstract ContextStoreType type();

    public ContextStore(KRedisHolder kRedisHolder) {
        this.prefix = kRedisHolder.getCommKeyPrefix("context-" + type().getType());
        this.redissonClient = kRedisHolder.getRedissonClient();
    }

    public RMap<String, String> getStore(String id) {
        return redissonClient.getMap(getKey(id));
    }

    @NotNull
    private String getKey(String id) {
        return prefix + id;
    }

    public void remove(String id) {
        getStore(id).delete();
    }

    public Map<String, String> getContextData(String id) {
        RMap<String, String> redisMap = getStore(id);
        return redisMap.readAllMap();
    }

    public void save(String id, Map<String, String> data, Duration duration) {
        RMap<String, String> redisMap = getStore(id);
        redisMap.clear();
        redisMap.putAll(data);
        redisMap.expire(duration);
    }


}
