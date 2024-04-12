package io.ihankun.framework.redis.core.type;

import io.ihankun.framework.redis.key.ICacheKey;

import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public interface StringCache {

    /**
     * 保存
     */
    boolean save(ICacheKey key, String value, Long expire);

    /**
     * 如果为空设置值，并返回true；如果已经存在的话直接返回false
     */
    boolean setIfAbsent(ICacheKey key, String value, long timeout, TimeUnit unit);

    /**
     * 保存
     */
    boolean save(ICacheKey key, String value, Long expire, TimeUnit timeUnit);

    /**
     * 获取所有缓存数据
     */
    String get(ICacheKey key);

    /**
     * 删除缓存
     */
    boolean del(ICacheKey key);

    /**
     * 更新缓存
     */
    boolean update(ICacheKey key, String value, Long expire, TimeUnit timeUnit);

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
     * 原子自增或自减
     */
    Long atomic(ICacheKey key, Long num,Long expire,TimeUnit timeUnit);
}
