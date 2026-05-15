package io.hankun.framework.cache.core.impl;

import io.hankun.framework.cache.core.type.MapCache;
import io.hankun.framework.cache.enums.RedisDataType;
import io.hankun.framework.cache.key.ICacheKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class RedisMapCacheImpl <K, V> extends AbstractRedisCache implements MapCache<K, V> {

    @Override
    public V getValue(ICacheKey ICacheKey, K mapKey) {
        try {
            return (V) getRedisTemplate().opsForHash().entries(ICacheKey.get()).get(mapKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean put(ICacheKey ICacheKey, K mapKey, V value) {
        return put(ICacheKey, mapKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean put(ICacheKey ICacheKey, K mapKey, V value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
    public boolean remove(ICacheKey ICacheKey, K mapKey) {
        try {
            getRedisTemplate().opsForHash().delete(ICacheKey.get(), mapKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Long size(ICacheKey ICacheKey) {
        try {
            return getRedisTemplate().opsForHash().size(ICacheKey.get());
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
    public boolean putAll(ICacheKey ICacheKey, Map<K, V> map) {
        return putAll(ICacheKey, map, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean putAll(ICacheKey ICacheKey, Map<K, V> map, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
    public boolean save(ICacheKey ICacheKey, Map<K, V> value, Long expire) {
        String key = ICacheKey.get();
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
    public Map<K, V> get(ICacheKey ICacheKey) {
        try {
            return (Map<K, V>) getRedisTemplate().opsForHash().entries(ICacheKey.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean del(ICacheKey ICacheKey) {
        try {
            getRedisTemplate().delete(ICacheKey.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public long delRawHashKeys(ICacheKey ICacheKey, Object... hashKeys) {
        try {
            return getRedisTemplate().opsForHash().delete(ICacheKey.get(), hashKeys);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public boolean update(ICacheKey ICacheKey, Map<K, V> value) {
        return update(ICacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean update(ICacheKey ICacheKey, Map<K, V> value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
    public boolean expire(ICacheKey ICacheKey, Long expire) {
        try {
            getRedisTemplate().expire(ICacheKey.get(), expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exits(ICacheKey ICacheKey) {
        try {
            Long size = getRedisTemplate().opsForHash().size(ICacheKey.get());
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
    public boolean putIfAbsent(ICacheKey ICacheKey, K mapKey, V value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
