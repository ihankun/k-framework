package io.hankun.framework.redis.key.impl;

import io.hankun.framework.redis.key.AbstractCacheKey;
import io.hankun.framework.redis.key.ICacheKey;
import io.hankun.framework.core.utils.string.StringPool;

/**
 * @author hankun
 */
public class OrgICacheKey extends AbstractCacheKey implements ICacheKey {

    private final String businessCode;

    private String orgId;

    private String key;

    public OrgICacheKey(String businessCode) {
        this.businessCode = businessCode;
    }

    public static OrgICacheKey build(String businessCode) {
        return new OrgICacheKey(businessCode);
    }

    public OrgICacheKey orgId(String orgId) {
        this.orgId = orgId;
        return this;
    }

    public OrgICacheKey key(String key) {
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
