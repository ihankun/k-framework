package io.hankun.framework.redis.core.type;


import io.hankun.framework.redis.key.CacheKey;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public interface ListCache<V> {

    /**
     * 保存
     */
    boolean save(CacheKey key, List<V> value, Long expire);

    /**
     * 获取所有缓存数据
     */
    List<V> get(CacheKey key);

    /**
     * 删除缓存
     */
    boolean del(CacheKey key);

    /**
     * 更新缓存
     *
     * @param cacheKey
     * @param value
     * @return
     */
    boolean update(CacheKey cacheKey, List<V> value);

    /**
     * 更新缓存
     */
    boolean update(CacheKey key, List<V> value, Long expire, TimeUnit timeUnit);

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
     * 弹出所有元素
     */
    List<V> pop(CacheKey key, int size);

    /**
     * 追加元素
     */
    boolean add(CacheKey cacheKey, V value);

    /**
     * 追加元素
     */
    boolean add(CacheKey key, V value, Long expire, TimeUnit timeUnit);

    /**
     * 移除元素
     */
    boolean remove(CacheKey key, V value);
}
