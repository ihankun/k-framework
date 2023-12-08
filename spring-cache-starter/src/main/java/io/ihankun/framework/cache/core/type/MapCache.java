package io.ihankun.framework.cache.core.type;

import io.ihankun.framework.cache.key.ICacheKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public interface MapCache<K, V> {

    /**
     * 保存
     */
    boolean save(ICacheKey key, Map<K, V> value, Long expire);

    /**
     * 获取所有缓存数据
     */
    Map<K, V> get(ICacheKey key);

    /**
     * 删除缓存
     */
    boolean del(ICacheKey key);

    /**
     * 更新缓存
     */
    boolean update(ICacheKey key, Map<K, V> value, Long expire, TimeUnit timeUnit);

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
     * 得到某个实体
     */
    V getValue(ICacheKey key, K mapKey);

    /**
     * 新增元素
     */
    boolean put(ICacheKey key, K mapKey, V value, Long expire, TimeUnit timeUnit);

    /**
     * 移除元素
     */
    boolean remove(ICacheKey key, K mapKey);


    /**
     * 元素数量
     */
    Long size(ICacheKey key);

    /**
     * 插入全部
     */
    boolean putAll(ICacheKey key, Map<K, V> map,Long expire,TimeUnit timeUnit);
}
