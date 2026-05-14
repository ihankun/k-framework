package io.hankun.framework.redis.core.impl;

import io.hankun.framework.redis.core.type.ListCache;
import io.hankun.framework.redis.enums.RedisDataType;
import io.hankun.framework.redis.key.CacheKey;
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
    public List<V> pop(CacheKey cacheKey, int size) {
        try {
            String key = cacheKey.get();
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
    public boolean add(CacheKey cacheKey, V value) {
        return add(cacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean add(CacheKey cacheKey, V value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
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
    public boolean remove(CacheKey cacheKey, V value) {
        try {
            getRedisTemplate().opsForList().remove(cacheKey.get(), 1, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean save(CacheKey cacheKey, List<V> values, Long expire) {
        String key = cacheKey.get();
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
    public List<V> get(CacheKey cacheKey) {
        String key = cacheKey.get();
        ListOperations<String, V> ops = getRedisTemplate().opsForList();
        return ops.range(key, 0, ops.size(key));
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
    public boolean update(CacheKey cacheKey, List<V> value) {
        return update(cacheKey, value, getMaxExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public boolean update(CacheKey cacheKey, List<V> value, Long expire, TimeUnit timeUnit) {
        String key = cacheKey.get();
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
            Long size = getRedisTemplate().opsForList().size(cacheKey.get());
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
