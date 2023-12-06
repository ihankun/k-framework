package io.ihankun.framework.cache.v1.core.type;


import io.ihankun.framework.cache.v1.key.CacheKey;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public interface ListCache<V> {

    /**
     * 保存
     *
     * @param key    缓存key
     * @param value  缓存value
     * @param expire 过期时间
     * @return
     */
    boolean save(CacheKey key, List<V> value, Long expire);

    /**
     * 获取所有缓存数据
     *
     * @param key
     * @return
     */
    List<V> get(CacheKey key);

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
    boolean update(CacheKey key, List<V> value);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @param expire
     * @param timeUnit
     * @return
     */
    boolean update(CacheKey key, List<V> value, Long expire, TimeUnit timeUnit);

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
     * 弹出所有元素
     *
     * @param key
     * @param size
     * @return
     */
    List<V> pop(CacheKey key, int size);

    /**
     * 追加元素
     *
     * @param key
     * @param value
     * @return
     */
    @Deprecated
    boolean add(CacheKey key, V value);

    /**
     * 追加元素
     *
     * @param key
     * @param value
     * @param expire
     * @param timeUnit
     * @return
     */
    boolean add(CacheKey key, V value, Long expire, TimeUnit timeUnit);

    /**
     * 移除元素
     *
     * @param key
     * @param value
     * @return
     */
    boolean remove(CacheKey key, V value);
}
