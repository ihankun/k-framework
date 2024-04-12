package io.ihankun.framework.redis.core.impl;

import io.ihankun.framework.redis.core.type.SetCache;
import io.ihankun.framework.redis.enums.RedisDataType;
import io.ihankun.framework.redis.key.ICacheKey;
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
    public List<String> pop(ICacheKey key, int size) {
        try {
            return (List<String>) getRedisTemplate().opsForSet().pop(key.get(), size);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean contain(ICacheKey key, V value) {
        try {
            return getRedisTemplate().opsForSet().members(key.get()).contains(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean put(ICacheKey key, V value, Long expire, TimeUnit timeUnit) {
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForSet().add(key.get(), value);
            getRedisTemplate().expire(key.get(), expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean putAll(ICacheKey key, Set<V> values, Long expire, TimeUnit timeUnit) {
        validate(key, values, expire, timeUnit);
        try {
            values.forEach(item -> {
                getRedisTemplate().opsForSet().add(key.get(), item);
            });
            getRedisTemplate().expire(key.get(), expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(ICacheKey key, V value) {
        try {
            getRedisTemplate().opsForSet().remove(key.get(), value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Long size(ICacheKey key) {
        try {
            return getRedisTemplate().opsForSet().size(key.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.SET;
    }

    @Override
    public boolean save(ICacheKey key, Set<V> value, Long expire) {
        validate(key, value,expire,TimeUnit.SECONDS);
        try {
            String[] array = value.toArray(new String[0]);
            getRedisTemplate().opsForSet().add(key.get(), array);
            expire(key, expire);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Set<V> get(ICacheKey key) {
        try {
            return (Set<V>) getRedisTemplate().opsForSet().members(key.get());
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
    public boolean update(ICacheKey key, Set<V> value, Long expire, TimeUnit timeUnit) {
        validate(key, value,expire,timeUnit);
        try {
            value.forEach(item -> getRedisTemplate().opsForSet().add(key.get(), item));
            getRedisTemplate().expire(key.get(),expire,timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean expire(ICacheKey key, Long expire) {
        validate(key, null,expire,TimeUnit.SECONDS);
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
            Long size = getRedisTemplate().opsForSet().size(key.get());
            if (size == null || size == 0) {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}
