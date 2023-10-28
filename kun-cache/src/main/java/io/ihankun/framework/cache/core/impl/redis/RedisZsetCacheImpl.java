package io.ihankun.framework.cache.core.impl.redis;

import io.ihankun.framework.cache.RedisDataType;
import io.ihankun.framework.cache.core.ZsetCache;
import io.ihankun.framework.cache.key.CacheKey;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public class RedisZsetCacheImpl<V> extends AbstractRedisCache implements ZsetCache<V> {
    @Override
    public Boolean add(CacheKey key, V value, double score) {
        return getRedisTemplate().opsForZSet().add(key.get(),value,score);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeWithScores(CacheKey key, long begin, long size) {
        return getRedisTemplate().opsForZSet().rangeWithScores(key.get(),begin,size);
    }

    @Override
    public Long remove(CacheKey key, V value) {
        return getRedisTemplate().opsForZSet().remove(key.get(),value);
    }

    @Override
    public Boolean delete(CacheKey key) {
        return getRedisTemplate().delete(key.get());
    }

    @Override
    public Long size(CacheKey key) {
        return getRedisTemplate().opsForZSet().size(key.get());
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.ZSET;
    }



}
