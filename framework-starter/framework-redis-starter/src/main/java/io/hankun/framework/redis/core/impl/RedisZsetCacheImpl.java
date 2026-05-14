package io.hankun.framework.redis.core.impl;

import io.hankun.framework.redis.core.type.ZsetCache;
import io.hankun.framework.redis.enums.RedisDataType;
import io.hankun.framework.redis.key.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * @author hankun
 */
@Slf4j
public class RedisZsetCacheImpl<V> extends AbstractRedisCache implements ZsetCache<V> {

    @Override
    public Boolean add(CacheKey cacheKey, V value, double score) {
        return getRedisTemplate().opsForZSet().add(cacheKey.get(), value, score);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeWithScores(CacheKey cacheKey, long begin, long size) {
        return getRedisTemplate().opsForZSet().rangeWithScores(cacheKey.get(), begin, size);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(CacheKey cacheKey, double min, double max) {
        return getRedisTemplate().opsForZSet().rangeByScoreWithScores(cacheKey.get(), min, max);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(CacheKey cacheKey, double min, double max, long offset, long count) {
        return getRedisTemplate().opsForZSet().rangeByScoreWithScores(cacheKey.get(), min, max, offset, count);
    }

    @Override
    public Long removeRangeByScore(CacheKey cacheKey, double min, double max) {
        return getRedisTemplate().opsForZSet().removeRangeByScore(cacheKey.get(), min, max);
    }

    @Override
    public Long remove(CacheKey cacheKey, V value) {
        return getRedisTemplate().opsForZSet().remove(cacheKey.get(), value);
    }

    @Override
    public Boolean delete(CacheKey cacheKey) {
        return getRedisTemplate().delete(cacheKey.get());
    }

    @Override
    public Long size(CacheKey cacheKey) {
        return getRedisTemplate().opsForZSet().size(cacheKey.get());
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
