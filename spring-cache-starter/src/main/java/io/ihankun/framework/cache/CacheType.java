package io.ihankun.framework.cache;

/**
 * @author hankun
 */
public enum CacheType {

    /**
     * 基于redis做缓存
     */
    REDIS("redis"),
    /**
     * 基于内存做缓存
     */
    MEMORY("memory");

    private String type;

    CacheType(String type) {
        this.type = type;
    }
}
