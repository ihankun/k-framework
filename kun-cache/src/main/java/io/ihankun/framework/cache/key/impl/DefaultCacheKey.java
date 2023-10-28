package io.ihankun.framework.cache.key.impl;


import io.ihankun.framework.cache.key.CacheKey;

/**
 * @author hankun
 */
public class DefaultCacheKey implements CacheKey {

    private static final String SPLIT = ":";

    private String businessCode;


    private String key;

    public DefaultCacheKey(String businessCode) {
        this.businessCode = businessCode;
    }


    public static final DefaultCacheKey build(String businessCode) {
        DefaultCacheKey key = new DefaultCacheKey(businessCode);
        return key;
    }

    public DefaultCacheKey key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public String get() {
        StringBuilder builder = new StringBuilder();
        builder.append(businessCode).append(SPLIT).append(key);
        return builder.toString();
    }
}
