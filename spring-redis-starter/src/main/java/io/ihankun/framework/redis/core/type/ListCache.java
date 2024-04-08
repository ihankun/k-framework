package io.ihankun.framework.redis.core.type;


import io.ihankun.framework.redis.key.ICacheKey;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public interface ListCache<V> {

    /**
     * 保存
     */
    boolean save(ICacheKey key, List<V> value, Long expire);

    /**
     * 获取所有缓存数据
     */
    List<V> get(ICacheKey key);

    /**
     * 删除缓存
     */
    boolean del(ICacheKey key);

    /**
     * 更新缓存
     */
    boolean update(ICacheKey key, List<V> value, Long expire, TimeUnit timeUnit);

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
     * 弹出所有元素
     */
    List<V> pop(ICacheKey key, int size);

    /**
     * 追加元素
     */
    boolean add(ICacheKey key, V value, Long expire, TimeUnit timeUnit);

    /**
     * 移除元素
     */
    boolean remove(ICacheKey key, V value);
}
