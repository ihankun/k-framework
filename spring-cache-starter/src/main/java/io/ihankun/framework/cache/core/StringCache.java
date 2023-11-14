package io.ihankun.framework.cache.core;


import io.ihankun.framework.cache.key.CacheKey;

import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
public interface StringCache {

    /**
     * 保存
     *
     * @param key    缓存key
     * @param value  缓存value
     * @param expire 过期时间
     * @return
     */
    boolean save(CacheKey key, String value, Long expire);

    /**
     * 如果为空设置值，并返回true；如果已经存在的话直接返回false
     *
     * @param key
     * @param value
     * @return
     */
    @Deprecated
    boolean setIfAbsent(CacheKey key, String value);

    /**
     * 如果为空设置值，并返回true；如果已经存在的话直接返回false
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     * @return
     */
    boolean setIfAbsent(CacheKey key, String value, long timeout, TimeUnit unit);

    /**
     * 保存
     *
     * @param key      缓存key
     * @param value    缓存value
     * @param expire   过期时间
     * @param timeUnit 过期时间单位
     * @return 是否设置成功
     */
    boolean save(CacheKey key, String value, Long expire, TimeUnit timeUnit);

    /**
     * 获取所有缓存数据
     *
     * @param key
     * @return
     */
    String get(CacheKey key);

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
    boolean update(CacheKey key, String value);

    /**
     * 更新缓存
     *
     * @param key
     * @param value
     * @param expire
     * @param timeUnit
     * @return
     */
    boolean update(CacheKey key, String value, Long expire, TimeUnit timeUnit);

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
     * 原子自增或自减
     *
     * @param key
     * @param num 正数为自增，负数为自减
     * @return 返回处理后数据
     */
    @Deprecated
    Long atomic(CacheKey key, Long num);


    /**
     * 原子自增或自减
     *
     * @param key
     * @param num 正数为自增，负数为自减
     * @param expire
     * @param timeUnit
     * @return 返回处理后数据
     */
    Long atomic(CacheKey key, Long num,Long expire,TimeUnit timeUnit);
}
