package io.hankun.framework.redis.core.type;

import io.hankun.framework.redis.key.CacheKey;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public interface SetCache<V> {

    /**
     * 保存
     */
    boolean save(CacheKey key, Set<V> value, Long expire);

    /**
     * 获取所有缓存数据
     */
    Set<V> get(CacheKey key);

    /**
     * 删除缓存
     */
    boolean del(CacheKey key);

    /**
     * 更新缓存
     */
    boolean update(CacheKey cacheKey, Set<V> value);

    /**
     * 更新缓存
     */
    boolean update(CacheKey key, Set<V> value, Long expire, TimeUnit timeUnit);

    /**
     * 修改过期时间
     */
    boolean expire(CacheKey key, Long expire);

    /**
     * 缓存是否存在
     */
    boolean exits(CacheKey key);

    //---------- 通用方法结束 ----------//

    /**
     * 弹出元素
     */
    List<String> pop(CacheKey key, int size);

    /**
     * 是否包含某个元素
     */
    boolean contain(CacheKey key, V value);

    /**
     * 插入元素
     */
    boolean put(CacheKey cacheKey, V value);

    /**
     * 插入元素
     */
    boolean put(CacheKey key, V value, Long expire, TimeUnit timeUnit);

    /**
     * 插入全部元素
     */
    boolean putAll(CacheKey cacheKey, Set<V> values);

    /**
     * 插入全部元素
     */
    boolean putAll(CacheKey key, Set<V> values, Long expire, TimeUnit timeUnit);

    /**
     * 移除元素
     */
    boolean remove(CacheKey key, V value);

    /**
     * 元素个数
     */
    Long size(CacheKey key);
}
