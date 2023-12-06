package io.ihankun.framework.cache.v1.key.impl;


import io.ihankun.framework.cache.v1.key.AbstractCacheKey;
import io.ihankun.framework.cache.v1.key.CacheKey;

/**
 * @author hankun
 */
public class OrgCacheKey extends AbstractCacheKey implements CacheKey {

    private static final String SPLIT = ":";

    private final String businessCode;

    private String orgId;

    private String key;

    public OrgCacheKey(String businessCode) {
        this.businessCode = businessCode;
    }

    public static OrgCacheKey build(String businessCode) {
        return new OrgCacheKey(businessCode);
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
        return formatKey(businessCode + SPLIT + orgId + SPLIT + key);
    }
}
