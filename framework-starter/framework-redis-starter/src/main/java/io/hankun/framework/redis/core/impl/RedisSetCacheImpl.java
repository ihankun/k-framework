package io.hankun.framework.redis.core.impl;

import io.hankun.framework.redis.core.type.SetCache;
import io.hankun.framework.redis.enums.RedisDataType;
import io.hankun.framework.redis.key.ICacheKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class RedisSetCacheImpl<V> extends AbstractRedisCache implements SetCache<V> {

    @Override
    public List<String> pop(ICacheKey cacheKey, int size) {
        try {
            return (List<String>) getRedisTemplate().opsForSet().pop(cacheKey.get(), size);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean contain(ICacheKey cacheKey, V value) {
        try {
            Set<String> members = getRedisTemplate().opsForSet().members(cacheKey.get());
            return members != null && members.contains(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean put(ICacheKey cacheKey, V value) {
        return put(cacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean put(ICacheKey cacheKey, V value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForSet().add(key, value);
            getRedisTemplate().expire(key, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean putAll(ICacheKey cacheKey, Set<V> values) {
        return putAll(cacheKey, values, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean putAll(ICacheKey cacheKey, Set<V> values, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, values, expire, timeUnit);
        try {
            values.forEach(item -> {
                getRedisTemplate().opsForSet().add(key, item);
            });
            getRedisTemplate().expire(key, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(ICacheKey cacheKey, V value) {
        try {
            getRedisTemplate().opsForSet().remove(cacheKey.get(), value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Long size(ICacheKey cacheKey) {
        try {
            return getRedisTemplate().opsForSet().size(cacheKey.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public boolean save(ICacheKey cacheKey, Set<V> value, Long expire) {
        String key = cacheKey.get();
        validate(key, value, expire, TimeUnit.SECONDS);
        try {
            String[] array = value.toArray(new String[0]);
            getRedisTemplate().opsForSet().add(key, array);
            getRedisTemplate().expire(key, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Set<V> get(ICacheKey cacheKey) {
        try {
            return (Set<V>) getRedisTemplate().opsForSet().members(cacheKey.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean del(ICacheKey cacheKey) {
        try {
            getRedisTemplate().delete(cacheKey.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean update(ICacheKey cacheKey, Set<V> value) {
        return update(cacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean update(ICacheKey cacheKey, Set<V> value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            value.forEach(item -> getRedisTemplate().opsForSet().add(key, item));
            getRedisTemplate().expire(key, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean expire(ICacheKey cacheKey, Long expire) {
        try {
            getRedisTemplate().expire(cacheKey.get(), expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exits(ICacheKey cacheKey) {
        try {
            Long size = getRedisTemplate().opsForSet().size(cacheKey.get());
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
    protected long getSizeInternal(String key) {
        try {
            return getRedisTemplate().opsForSet().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.SET;
    }
}
