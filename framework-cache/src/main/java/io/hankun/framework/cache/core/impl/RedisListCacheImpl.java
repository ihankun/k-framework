package io.hankun.framework.cache.core.impl;

import io.hankun.framework.cache.core.type.ListCache;
import io.hankun.framework.cache.enums.RedisDataType;
import io.hankun.framework.cache.key.ICacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class RedisListCacheImpl <V> extends AbstractRedisCache implements ListCache<V> {

    @Override
    public List<V> pop(ICacheKey ICacheKey, int size) {
        try {
            String key = ICacheKey.get();
            List<V> list = new ArrayList<>();
            while (size != 0) {
                V pop = (V) getRedisTemplate().opsForList().leftPop(key);
                list.add(pop);
                size--;
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean add(ICacheKey ICacheKey, V value) {
        return add(ICacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean add(ICacheKey ICacheKey, V value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().opsForList().leftPush(key, value);
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
            getRedisTemplate().opsForList().remove(ICacheKey.get(), 1, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean save(ICacheKey ICacheKey, List<V> values, Long expire) {
        String key = ICacheKey.get();
        validate(key, values, expire, TimeUnit.SECONDS);
        try {
            getRedisTemplate().opsForList().leftPushAll(key, values);
            getRedisTemplate().expire(key, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public List<V> get(ICacheKey ICacheKey) {
        String key = ICacheKey.get();
        ListOperations<String, V> ops = getRedisTemplate().opsForList();
        return ops.range(key, 0, ops.size(key));
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
    public boolean update(ICacheKey ICacheKey, List<V> value) {
        return update(ICacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean update(ICacheKey ICacheKey, List<V> value, Long expire, TimeUnit timeUnit) {
        String key = ICacheKey.get();
        validate(key, value, expire, timeUnit);
        try {
            getRedisTemplate().delete(key);
            getRedisTemplate().opsForList().leftPushAll(key, value);
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
            Long size = getRedisTemplate().opsForList().size(ICacheKey.get());
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
            return getRedisTemplate().opsForList().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }

    @Override
    protected RedisDataType dataType() {
        return RedisDataType.LIST;
    }
}
