package io.ihankun.framework.captcha.store.impl;

import io.ihankun.framework.captcha.cache.ConCurrentExpiringMap;
import io.ihankun.framework.captcha.cache.ExpiringMap;
import io.ihankun.framework.captcha.store.CacheStore;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public class LocalCacheStore implements CacheStore {

    protected ExpiringMap<String, Map<String, Object>> cache;

    public LocalCacheStore() {
        cache = new ConCurrentExpiringMap<>();
        cache.init();
    }

    @Override
    public Map<String, Object> getCache(String key) {
        return cache.get(key);
    }

    @Override
    public Map<String, Object> getAndRemoveCache(String key) {
        return cache.remove(key);
    }

    @Override
    public boolean setCache(String key, Map<String, Object> data, Long expire, TimeUnit timeUnit) {
        cache.remove(key);
        cache.put(key, data, expire, timeUnit);
        return true;
    }
}
