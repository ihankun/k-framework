package io.hankun.framework.redis.core.impl;

import io.hankun.framework.redis.core.type.StringCache;
import io.hankun.framework.redis.enums.RedisDataType;
import io.hankun.framework.redis.key.ICacheKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class RedisStringCacheImpl extends AbstractRedisCache implements StringCache {

    @Override
    public boolean save(ICacheKey cacheKey, String value, Long expire) {
        return save(cacheKey, value, expire, TimeUnit.SECONDS);
    }

    @Override
    public boolean setIfAbsent(ICacheKey cacheKey, String value) {
        return setIfAbsent(cacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean setIfAbsent(ICacheKey cacheKey, String value, long timeout, TimeUnit unit) {
        String key = cacheKey.get();
        validate(key, value, timeout, unit);
        return getRedisTemplate().opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 保存
     *
     * @param cacheKey 缓存key
     * @param value    缓存value
     * @param expire   过期时间
     * @param timeUnit 过期时间单位
     * @return 是否设置成功
     */
    @Override
    public boolean save(ICacheKey cacheKey, String value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForValue().set(key, value, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public String get(ICacheKey cacheKey) {
        Object value = getRedisTemplate().opsForValue().get(cacheKey.get());
        return value == null ? null : value.toString();
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
    public boolean update(ICacheKey cacheKey, String value) {
        return update(cacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean update(ICacheKey cacheKey, String value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForValue().set(key, value, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean expire(ICacheKey cacheKey, Long expire) {
        String key = cacheKey.get();
        try {
            getRedisTemplate().expire(key, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exits(ICacheKey cacheKey) {
        try {
            Object value = getRedisTemplate().opsForValue().get(cacheKey.get());
            return !ObjectUtils.isEmpty(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Long atomic(ICacheKey cacheKey, Long num) {
        return atomic(cacheKey, num, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public Long atomic(ICacheKey cacheKey, Long num, Long expire, TimeUnit timeUnit) {
        if (num == 0) {
            return null;
        }

        String key = cacheKey.get();
        validate(key, num, expire, timeUnit);
        try {
            if (num > 0) {
                Long increment = getRedisTemplate().opsForValue().increment(key, num);
                getRedisTemplate().expire(key, getMaxExpireTime(), TimeUnit.MINUTES);
                return increment;
            }

            Long decrement = getRedisTemplate().opsForValue().decrement(key, Math.abs(num));
            getRedisTemplate().expire(key, getMaxExpireTime(), TimeUnit.MINUTES);
            return decrement;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected long getSizeInternal(String key) {
        try {
            return getRedisTemplate().opsForValue().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.STRING;
    }
}
