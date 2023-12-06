package io.ihankun.framework.cache.v1.core.impl.redis;

import io.ihankun.framework.cache.v1.comm.RedisDataType;
import io.ihankun.framework.cache.v1.core.type.StringCache;
import io.ihankun.framework.cache.v1.key.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class RedisStringCacheImpl extends AbstractRedisCache implements StringCache {

    @Override
    public boolean save(CacheKey key, String value, Long expire) {
        return save(key, value, expire, TimeUnit.SECONDS);
    }

    @Override
    public boolean setIfAbsent(CacheKey key, String value) {
        return setIfAbsent(key, value, getMaxExpireTime(), TimeUnit.DAYS);
    }

    @Override
    public boolean setIfAbsent(CacheKey key, String value, long timeout, TimeUnit unit) {
        validate(key, value, timeout, unit);
        return getRedisTemplate().opsForValue().setIfAbsent(key.get(), value, timeout, unit);
    }

    /**
     * 保存
     *
     * @param key      缓存key
     * @param value    缓存value
     * @param expire   过期时间
     * @param timeUnit 过期时间单位
     * @return 是否设置成功
     */
    @Override
    public boolean save(CacheKey key, String value, Long expire, TimeUnit timeUnit) {
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForValue().set(key.get(), value, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public String get(CacheKey key) {
        Object value = getRedisTemplate().opsForValue().get(key.get());
        return value == null ? null : value.toString();
    }

    @Override
    public boolean del(CacheKey key) {
        try {
            getRedisTemplate().delete(key.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean update(CacheKey key, String value) {
        return update(key, value, getMaxExpireTime(), TimeUnit.DAYS);
    }

    @Override
    public boolean update(CacheKey key, String value, Long expire, TimeUnit timeUnit) {
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForValue().set(key.get(), value, expire, timeUnit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean expire(CacheKey key, Long expire) {
        validate(key, null, expire, TimeUnit.SECONDS);
        try {
            getRedisTemplate().expire(key.get(), expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exits(CacheKey key) {
        try {
            Object value = getRedisTemplate().opsForValue().get(key.get());
            return !ObjectUtils.isEmpty(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Long atomic(CacheKey key, Long num) {
        return atomic(key, num, getMaxExpireTime(), TimeUnit.DAYS);
    }

    @Override
    public Long atomic(CacheKey key, Long num, Long expire, TimeUnit timeUnit) {
        validate(key, num, expire, timeUnit);
        try {

            if (num == 0) {
                return null;
            }

            if (num > 0) {
                Long increment = getRedisTemplate().opsForValue().increment(key.get(), num);
                getRedisTemplate().expire(key.get(), getMaxExpireTime(), TimeUnit.DAYS);
                return increment;
            }


            Long decrement = getRedisTemplate().opsForValue().decrement(key.get(), Math.abs(num));
            getRedisTemplate().expire(key.get(), getMaxExpireTime(), TimeUnit.DAYS);
            return decrement;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected Long size(CacheKey key) {
        try {
            return getRedisTemplate().opsForValue().size(key.get());
        } catch (Exception e) {
            log.error("string 获取大小失败", e);
        }
        return 0L;
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.STRING;
    }
}
