package io.ihankun.framework.cache.core.type;

import io.ihankun.framework.cache.key.ICacheKey;

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
    boolean save(ICacheKey key, Set<V> value, Long expire);

    /**
     * 获取所有缓存数据
     */
    Set<V> get(ICacheKey key);

    /**
     * 删除缓存
     */
    boolean del(ICacheKey key);

    /**
     * 更新缓存
     */
    boolean update(ICacheKey key, Set<V> value, Long expire, TimeUnit timeUnit);

    /**
     * 修改过期时间
     */
    boolean expire(ICacheKey key, Long expire);

    /**
     * 缓存是否存在
     */
    boolean exits(ICacheKey key);

    //---------- 通用方法结束 ----------//

    /**
     * 弹出元素
     */
    List<String> pop(ICacheKey key, int size);

    /**
     * 是否包含某个元素
     */
    boolean contain(ICacheKey key, V value);

    /**
     * 插入元素
     */
    boolean put(ICacheKey key, V value, Long expire, TimeUnit timeUnit);

    /**
     * 插入全部元素
     */
    boolean putAll(ICacheKey key, Set<V> values, Long expire, TimeUnit timeUnit);

    /**
     * 移除元素
     */
    boolean remove(ICacheKey key, V value);

    /**
     * 元素个数
     */
    Long size(ICacheKey key);
}
