package io.ihankun.framework.redis.core.impl;

import io.ihankun.framework.redis.core.type.MapCache;
import io.ihankun.framework.redis.enums.RedisDataType;
import io.ihankun.framework.redis.key.ICacheKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class RedisMapCacheImpl <K, V> extends AbstractRedisCache implements MapCache<K, V> {

    @Override
    public V getValue(ICacheKey key, K mapKey) {
        try {
            return (V) getRedisTemplate().opsForHash().entries(key.get()).get(mapKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean put(ICacheKey key, K mapKey, V value, Long expire, TimeUnit timeUnit) {
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForHash().put(key.get(), mapKey, value);
            getRedisTemplate().expire(key.get(), expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(ICacheKey key, K mapKey) {
        try {
            getRedisTemplate().opsForHash().delete(key.get(), mapKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Long size(ICacheKey key) {
        try {
            return getRedisTemplate().opsForHash().size(key.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.MAP;
    }

    @Override
    public boolean putAll(ICacheKey key, Map<K, V> map, Long expire, TimeUnit timeUnit) {
        validate(key, map, expire, timeUnit);
        try {
            getRedisTemplate().opsForHash().putAll(key.get(), map);
            getRedisTemplate().expire(key.get(), expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    @Override
    public boolean save(ICacheKey key, Map<K, V> value, Long expire) {
        validate(key, value,expire,TimeUnit.SECONDS);
        try {
            getRedisTemplate().opsForHash().putAll(key.get(), value);
            expire(key, expire);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Map<K, V> get(ICacheKey key) {
        try {
            return (Map<K, V>) getRedisTemplate().opsForHash().entries(key.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean del(ICacheKey key) {
        try {
            getRedisTemplate().delete(key.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean update(ICacheKey key, Map<K, V> value, Long expire, TimeUnit timeUnit) {
        validate(key, value,expire,timeUnit);
        try {
            value.entrySet().forEach(item -> getRedisTemplate().opsForHash().put(key.get(), item.getKey(), item.getValue()));
            getRedisTemplate().expire(key.get(),expire,timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    @Override
    public boolean expire(ICacheKey key, Long expire) {
        try {
            getRedisTemplate().expire(key.get(), expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exits(ICacheKey key) {
        try {
            Long size = getRedisTemplate().opsForHash().size(key.get());
            if (size == 0) {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
