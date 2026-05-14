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
    public boolean save(ICacheKey ICacheKey, String value, Long expire) {
        return save(ICacheKey, value, expire, TimeUnit.SECONDS);
    }

    @Override
    public boolean setIfAbsent(ICacheKey ICacheKey, String value) {
        return setIfAbsent(ICacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean setIfAbsent(ICacheKey ICacheKey, String value, long timeout, TimeUnit unit) {
        String key = ICacheKey.get();
        validate(key, value, timeout, unit);
        return getRedisTemplate().opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 保存
     *
     * @param ICacheKey 缓存key
     * @param value    缓存value
     * @param expire   过期时间
     * @param timeUnit 过期时间单位
     * @return 是否设置成功
     */
    @Override
    public boolean save(ICacheKey ICacheKey, String value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
    public String get(ICacheKey ICacheKey) {
        Object value = getRedisTemplate().opsForValue().get(ICacheKey.get());
        return value == null ? null : value.toString();
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
    public boolean update(ICacheKey ICacheKey, String value) {
        return update(ICacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean update(ICacheKey ICacheKey, String value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
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
    public boolean expire(ICacheKey ICacheKey, Long expire) {
        String key = ICacheKey.get();
        try {
            getRedisTemplate().expire(key, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exits(ICacheKey ICacheKey) {
        try {
            Object value = getRedisTemplate().opsForValue().get(ICacheKey.get());
            return !ObjectUtils.isEmpty(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Long atomic(ICacheKey ICacheKey, Long num) {
        return atomic(ICacheKey, num, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public Long atomic(ICacheKey ICacheKey, Long num, Long expire, TimeUnit timeUnit) {
        if (num == 0) {
            return null;
        }

        String key = ICacheKey.get();
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
