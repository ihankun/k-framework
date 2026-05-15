package io.hankun.framework.cache.key.impl;

import io.hankun.framework.cache.key.AbstractCacheKey;
import io.hankun.framework.cache.key.ICacheKey;
import io.hankun.framework.core.utils.string.StringPool;

/**
 * @author hankun
 */
public class OrgCacheKey extends AbstractCacheKey implements ICacheKey {

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
        return domainFormatKey(businessCode + StringPool.COLON + orgId + StringPool.COLON + key);
    }

    @Override
    public String getPrefix() {
        return "";
    }
}
