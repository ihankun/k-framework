package io.ihankun.framework.cache.key.impl;


import io.ihankun.framework.cache.key.CacheKey;

/**
 * @author hankun
 */
public class OrgCacheKey implements CacheKey {

    private static final String SPLIT = ":";

    private String businessCode;

    private String orgId;

    private String key;

    public OrgCacheKey(String businessCode) {
        this.businessCode = businessCode;
    }

    public static final OrgCacheKey build(String businessCode) {
        OrgCacheKey key = new OrgCacheKey(businessCode);
        return key;
    }

    public OrgCacheKey orgId(String orgId) {
        this.orgId = orgId;
        return this;
    }

    public OrgCacheKey key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public String get() {
        StringBuilder builder = new StringBuilder();
        builder.append(businessCode).append(SPLIT).append(orgId).append(SPLIT).append(key);
        return builder.toString();
    }
}
