package io.hankun.framework.cache.config;

import io.hankun.framework.redis.enums.CacheType;

/**
 * @author hankun
 */
public class CacheConfigProperties {

    /**
     * 缓存类型 REDIS？MEMORY
     */
    private CacheType type;

    /**
     * 缓存调用地址，如redis的地址
     */
    private String url;
}
