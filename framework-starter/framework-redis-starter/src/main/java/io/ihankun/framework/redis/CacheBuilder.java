package io.ihankun.framework.redis;


import io.ihankun.framework.redis.core.impl.*;
import io.ihankun.framework.redis.enums.CacheType;

/**
 * @author hankun
 */
public class CacheBuilder {

    /**
     * 构造redis管理器
     */
    public static CacheManager build() {
        return new CacheManager(new RedisStringCacheImpl(), new RedisMapCacheImpl(), new RedisListCacheImpl(), new RedisSetCacheImpl(),new RedisZsetCacheImpl());
    }

    /**
     * 构造不同类型的cache管理器
     */
    public static CacheManager build(CacheType type) {
        CacheManager manager = null;
        if (type.equals(CacheType.REDIS)) {
            manager = new CacheManager(new RedisStringCacheImpl(), new RedisMapCacheImpl(), new RedisListCacheImpl(), new RedisSetCacheImpl(),new RedisZsetCacheImpl());
        }
        return manager;
    }
}
