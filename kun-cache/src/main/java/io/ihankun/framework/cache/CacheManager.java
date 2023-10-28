package io.ihankun.framework.cache;


import io.ihankun.framework.cache.core.*;

/**
 * @author hankun
 */
public class CacheManager<K, V> {

    private StringCache stringCache;
    private MapCache<K, V> mapCache;
    private ListCache<V> listCache;
    private SetCache<V> setCache;
    private ZsetCache<V> zsetCache;

    public CacheManager(StringCache stringCache, MapCache mapCache, ListCache listCache, SetCache setCache, ZsetCache zsetCache) {
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
