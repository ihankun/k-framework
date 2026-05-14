package io.hankun.framework.redis.core.impl;

import io.hankun.framework.redis.core.type.MapCache;
import io.hankun.framework.redis.enums.RedisDataType;
import io.hankun.framework.redis.key.CacheKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class RedisMapCacheImpl <K, V> extends AbstractRedisCache implements MapCache<K, V> {

    @Override
    public V getValue(CacheKey cacheKey, K mapKey) {
        try {
            return (V) getRedisTemplate().opsForHash().entries(cacheKey.get()).get(mapKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean put(CacheKey cacheKey, K mapKey, V value) {
        return put(cacheKey, mapKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean put(CacheKey cacheKey, K mapKey, V value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForHash().put(key, mapKey, value);
            getRedisTemplate().expire(key, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(CacheKey cacheKey, K mapKey) {
        try {
            getRedisTemplate().opsForHash().delete(cacheKey.get(), mapKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Long size(CacheKey cacheKey) {
        try {
            return getRedisTemplate().opsForHash().size(cacheKey.get());
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
    public boolean putAll(CacheKey cacheKey, Map<K, V> map) {
        return putAll(cacheKey, map, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean putAll(CacheKey cacheKey, Map<K, V> map, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, map, expire, timeUnit);
        try {
            getRedisTemplate().opsForHash().putAll(key, map);
            getRedisTemplate().expire(key, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean save(CacheKey cacheKey, Map<K, V> value, Long expire) {
        String key = cacheKey.get();
        validate(key, value, expire, TimeUnit.SECONDS);
        try {
            getRedisTemplate().opsForHash().putAll(key, value);
            getRedisTemplate().expire(key, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Map<K, V> get(CacheKey cacheKey) {
        try {
            return (Map<K, V>) getRedisTemplate().opsForHash().entries(cacheKey.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean del(CacheKey cacheKey) {
        try {
            getRedisTemplate().delete(cacheKey.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public long delRawHashKeys(CacheKey cacheKey, Object... hashKeys) {
        try {
            return getRedisTemplate().opsForHash().delete(cacheKey.get(), hashKeys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public boolean update(CacheKey cacheKey, Map<K, V> value) {
        return update(cacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean update(CacheKey cacheKey, Map<K, V> value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            value.entrySet().forEach(item -> getRedisTemplate().opsForHash().put(key, item.getKey(), item.getValue()));
            getRedisTemplate().expire(key, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    @Override
    public boolean expire(CacheKey cacheKey, Long expire) {
        try {
            getRedisTemplate().expire(cacheKey.get(), expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exits(CacheKey cacheKey) {
        try {
            Long size = getRedisTemplate().opsForHash().size(cacheKey.get());
            if (size == null || size == 0) {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean putIfAbsent(CacheKey cacheKey, K mapKey, V value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForHash().putIfAbsent(key, mapKey, value);
            getRedisTemplate().expire(key, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    protected long getSizeInternal(String key) {
        try {
            return getRedisTemplate().opsForHash().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }
}
