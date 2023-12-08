package io.ihankun.framework.cache;

import io.ihankun.framework.cache.core.type.*;

/**
 * @author hankun
 */
public class CacheManager<K, V> {

    private final StringCache stringCache;
    private final MapCache<K, V> mapCache;
    private final ListCache<V> listCache;
    private final SetCache<V> setCache;
    private final ZsetCache<V> zsetCache;

    public CacheManager(StringCache stringCache, MapCache<K, V> mapCache, ListCache<V> listCache, SetCache<V> setCache, ZsetCache<V> zsetCache) {
        this.stringCache = stringCache;
        this.mapCache = mapCache;
        this.listCache = listCache;
        this.setCache = setCache;
        this.zsetCache = zsetCache;
    }

    public StringCache string() {
        return stringCache;
    }

    public MapCache<K, V> map() {
        return mapCache;
    }

    public SetCache<V> set() {
        return setCache;
    }

    public ListCache<V> list() {
        return listCache;
    }

    public ZsetCache<V> zset() {
        return zsetCache;
    }
}
