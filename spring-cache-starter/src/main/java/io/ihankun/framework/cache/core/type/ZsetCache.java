package io.ihankun.framework.cache.core.type;

import io.ihankun.framework.cache.key.ICacheKey;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * @author hankun
 */
public interface ZsetCache<V> {

    /**
     * 增加元素
     */
    Boolean add(ICacheKey key, V value, double score);

    /**
     * 根据分数获取值
     */
    Set<ZSetOperations.TypedTuple<V>> rangeWithScores(ICacheKey key, long begin, long size);

    /**
     * 删除元素
     */
    Long remove(ICacheKey key,V value);

    /**
     * 删除
     */
    Boolean del(ICacheKey key);

    /**
     * 获取元素个数
     */
    Long size(ICacheKey key);
}
