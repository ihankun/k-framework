package io.ihankun.framework.cache.core;

import io.ihankun.framework.cache.key.CacheKey;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * @author hankun
 */
public interface ZsetCache<V> {

    /**
     * 增加元素
     * @param key
     * @param value
     * @param score
     */
    Boolean add(CacheKey key, V value, double score);

    /**
     * 根据分数获取值
     * @param key
     * @param begin
     * @param size
     * @return
     */
    Set<ZSetOperations.TypedTuple<V>> rangeWithScores(CacheKey key, long begin, long size);

    /**
     * 删除元素
     * @param key
     * @param value
     * @return
     */
    Long remove(CacheKey key,V value);

    /**
     * 删除ZSET
     * @param key
     * @return
     */
    Boolean delete(CacheKey key);
    /**
     * 获取元素个数
     * @param key
     * @return
     */
    Long size(CacheKey key);
}
