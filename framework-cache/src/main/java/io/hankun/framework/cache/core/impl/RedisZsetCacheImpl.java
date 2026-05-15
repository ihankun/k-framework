package io.hankun.framework.cache.core.impl;

import io.hankun.framework.cache.core.type.ZsetCache;
import io.hankun.framework.cache.enums.RedisDataType;
import io.hankun.framework.cache.key.ICacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * @author hankun
 */
@Slf4j
public class RedisZsetCacheImpl<V> extends AbstractRedisCache implements ZsetCache<V> {

    @Override
    public Boolean add(ICacheKey ICacheKey, V value, double score) {
        return getRedisTemplate().opsForZSet().add(ICacheKey.get(), value, score);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeWithScores(ICacheKey ICacheKey, long begin, long size) {
        return getRedisTemplate().opsForZSet().rangeWithScores(ICacheKey.get(), begin, size);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(ICacheKey ICacheKey, double min, double max) {
        return getRedisTemplate().opsForZSet().rangeByScoreWithScores(ICacheKey.get(), min, max);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(ICacheKey ICacheKey, double min, double max, long offset, long count) {
        return getRedisTemplate().opsForZSet().rangeByScoreWithScores(ICacheKey.get(), min, max, offset, count);
    }

    @Override
    public Long removeRangeByScore(ICacheKey ICacheKey, double min, double max) {
        return getRedisTemplate().opsForZSet().removeRangeByScore(ICacheKey.get(), min, max);
    }

    @Override
    public Long remove(ICacheKey ICacheKey, V value) {
        return getRedisTemplate().opsForZSet().remove(ICacheKey.get(), value);
    }

    @Override
    public Boolean delete(ICacheKey ICacheKey) {
        return getRedisTemplate().delete(ICacheKey.get());
    }

    @Override
    public Long size(ICacheKey ICacheKey) {
        return getRedisTemplate().opsForZSet().size(ICacheKey.get());
    }

    @Override
    protected long getSizeInternal(String key) {
        try {
            return getRedisTemplate().opsForZSet().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.ZSET;
    }
}
