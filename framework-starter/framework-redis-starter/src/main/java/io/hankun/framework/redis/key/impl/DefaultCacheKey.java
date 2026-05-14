package io.hankun.framework.redis.key.impl;

import io.hankun.framework.redis.key.AbstractCacheKey;
import io.hankun.framework.redis.key.CacheKey;
import io.hankun.framework.core.utils.string.StringPool;

/**
 * @author hankun
 */
public class DefaultCacheKey extends AbstractCacheKey implements CacheKey {

    private final String businessCode;

    private String key;

    public DefaultCacheKey(String businessCode) {
        this.businessCode = businessCode;
    }

    public static DefaultCacheKey build(String businessCode) {
        return new DefaultCacheKey(businessCode);
    }

    public DefaultCacheKey key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public String get() {
        return domainFormatKey(businessCode + StringPool.COLON + key);
    }

    @Override
    public String getPrefix() {
        return "";
    }
}
