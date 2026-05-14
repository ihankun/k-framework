package io.hankun.framework.redis.key.impl;

import io.hankun.framework.redis.key.AbstractCacheKey;
import io.hankun.framework.redis.key.ICacheKey;
import io.hankun.framework.core.utils.string.StringPool;

/**
 * @author hankun
 */
public class DefaultICacheKey extends AbstractCacheKey implements ICacheKey {

    private final String businessCode;

    private String key;

    public DefaultICacheKey(String businessCode) {
        this.businessCode = businessCode;
    }

    public static DefaultICacheKey build(String businessCode) {
        return new DefaultICacheKey(businessCode);
    }

    public DefaultICacheKey key(String key) {
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
