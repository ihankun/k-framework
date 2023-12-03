package io.ihankun.framework.captcha.spring.store;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface CacheStore {

    /**
     * 读取缓存数据通过key
     *
     * @param key key
     * @return Map<String, Object>
     */
    Map<String, Object> getCache(String key);

    /**
     * 获取并删除数据 通过key
     *
     * @param key key
     * @return Map<String, Object>
     */
    Map<String, Object> getAndRemoveCache(String key);

    /**
     * 添加缓存数据
     *
     * @param key      key
     * @param data     data
     * @param expire   过期时间
     * @param timeUnit 过期时间单位
     * @return boolean
     */
    boolean setCache(String key, Map<String, Object> data, Long expire, TimeUnit timeUnit);
}
