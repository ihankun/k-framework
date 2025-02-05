package io.ihankun.framework.redis.core.impl;

import io.ihankun.framework.redis.core.type.ZsetCache;
import io.ihankun.framework.redis.enums.RedisDataType;
import io.ihankun.framework.redis.key.ICacheKey;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * @author hankun
 */
public class RedisZsetCacheImpl<V> extends AbstractRedisCache implements ZsetCache<V> {

    @Override
    public Boolean add(ICacheKey key, V value, double score) {
        return getRedisTemplate().opsForZSet().add(key.get(),value,score);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeWithScores(ICacheKey key, long begin, long size) {
        return getRedisTemplate().opsForZSet().rangeWithScores(key.get(),begin,size);
    }

    @Override
    public Long remove(ICacheKey key, V value) {
        return getRedisTemplate().opsForZSet().remove(key.get(),value);
    }

    @Override
    public Boolean del(ICacheKey key) {
        return getRedisTemplate().delete(key.get());
    }

    @Override
    public Long size(ICacheKey key) {
        return getRedisTemplate().opsForZSet().size(key.get());
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.ZSET;
    }



}
