package io.ihankun.framework.cache.core;


import io.ihankun.framework.cache.key.CacheKey;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public interface SetCache<V> {

    /**
     * 保存
     *
     * @param key    缓存key
     * @param value  缓存value
     * @param expire 过期时间
     * @return
     */
    boolean save(CacheKey key, Set<V> value, Long expire);

    /**
     * 获取所有缓存数据
     *
     * @param key
     * @return
     */
    Set<V> get(CacheKey key);

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    boolean del(CacheKey key);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @return
     */
    @Deprecated
    boolean update(CacheKey key, Set<V> value);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @param expire
     * @param timeUnit
     * @return
     */
    boolean update(CacheKey key, Set<V> value, Long expire, TimeUnit timeUnit);

    /**
     * 修改过期时间
     *
     * @param key
     * @param expire
     * @return
     */
    boolean expire(CacheKey key, Long expire);

    /**
     * 缓存是否存在
     *
     * @param key
     * @return
     */
    boolean exits(CacheKey key);

    //---------- 通用方法结束 ----------//

    /**
     * 弹出元素
     *
     * @param key
     * @param size
     * @return
     */
    List<String> pop(CacheKey key, int size);

    /**
     * 是否包含某个元素
     *
     * @param key
     * @param value
     * @return
     */
    boolean contain(CacheKey key, V value);

    /**
     * 插入元素
     *
     * @param key
     * @param value
     * @return
     */
    @Deprecated
    boolean put(CacheKey key, V value);

    /**
     * 插入元素
     *
     * @param key
     * @param value
     *
     * @return
     */
    boolean put(CacheKey key, V value, Long expire, TimeUnit timeUnit);

    /**
     * 插入全部元素
     *
     * @param key
     * @param values
     * @return
     */
    @Deprecated
    boolean putAll(CacheKey key, Set<V> values);

    /**
     * 插入全部元素
     *
     * @param key
     * @param values
     * @return
     */
    boolean putAll(CacheKey key, Set<V> values, Long expire, TimeUnit timeUnit);

    /**
     * 移除元素
     *
     * @param key
     * @param value
     * @return
     */
    boolean remove(CacheKey key, V value);

    /**
     * 元素个数
     *
     * @param key
     * @return
     */
    Long size(CacheKey key);
}
