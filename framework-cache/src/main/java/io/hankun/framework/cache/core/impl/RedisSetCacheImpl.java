package io.hankun.framework.cache.core.impl;

import io.hankun.framework.cache.core.type.SetCache;
import io.hankun.framework.cache.enums.RedisDataType;
import io.hankun.framework.cache.key.ICacheKey;
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
    public List<String> pop(ICacheKey ICacheKey, int size) {
        try {
            return (List<String>) getRedisTemplate().opsForSet().pop(ICacheKey.get(), size);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean contain(ICacheKey ICacheKey, V value) {
        try {
            Set<String> members = getRedisTemplate().opsForSet().members(ICacheKey.get());
            return members != null && members.contains(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean put(ICacheKey ICacheKey, V value) {
        return put(ICacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean put(ICacheKey ICacheKey, V value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
    public boolean putAll(ICacheKey ICacheKey, Set<V> values) {
        return putAll(ICacheKey, values, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean putAll(ICacheKey ICacheKey, Set<V> values, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
    public boolean remove(ICacheKey ICacheKey, V value) {
        try {
            getRedisTemplate().opsForSet().remove(ICacheKey.get(), value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Long size(ICacheKey ICacheKey) {
        try {
            return getRedisTemplate().opsForSet().size(ICacheKey.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public boolean save(ICacheKey ICacheKey, Set<V> value, Long expire) {
        String key = ICacheKey.get();
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
    public Set<V> get(ICacheKey ICacheKey) {
        try {
            return (Set<V>) getRedisTemplate().opsForSet().members(ICacheKey.get());
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
    public boolean update(ICacheKey ICacheKey, Set<V> value) {
        return update(ICacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean update(ICacheKey ICacheKey, Set<V> value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
            Long size = getRedisTemplate().opsForSet().size(ICacheKey.get());
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
