package io.ihankun.framework.redis.key.impl;

import io.ihankun.framework.redis.key.AbstractCacheKey;
import io.ihankun.framework.redis.key.ICacheKey;
import io.ihankun.framework.core.utils.string.StringPool;

/**
 * @author hankun
 */
public class DefaultCacheKey extends AbstractCacheKey implements ICacheKey {

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
