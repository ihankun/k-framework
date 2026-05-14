package io.hankun.framework.redis.core.type;

import io.hankun.framework.redis.key.ICacheKey;
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
     * 指定分数范围内的所有元素及其分数
     */
    Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(ICacheKey ICacheKey, double min, double max);

    /**
     * 分页查询 指定分数范围内的所有元素及其分数
     */
    Set<ZSetOperations.TypedTuple<V>> rangeByScoreWithScores(ICacheKey ICacheKey, double min, double max, long offset, long count);

    /**
     * 删除指定分数范围内的元素
     */
    Long removeRangeByScore(ICacheKey ICacheKey, double min, double max);

    /**
     * 删除元素
     */
    Long remove(ICacheKey key, V value);

    /**
     * 删除
     */
    Boolean delete(ICacheKey key);

    /**
     * 获取元素个数
     */
    Long size(ICacheKey key);
}
