package io.hankun.framework.ai.core.session.task;


import io.hankun.framework.commons.utils.ContextUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @className: MapConfig
 * @createAt: 2025/10/20 10:25
 * @author: hankun
 */
public record MapConfig(Map<String, Object> config) {

    public static MapConfig of() {
        return new MapConfig(new ConcurrentHashMap<>());
    }

    public <T> T get(String key, Class<T> clazz) {
        return ContextUtil.read(config, key, clazz, true);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        return ContextUtil.readList(config, key, clazz, true);
    }

    public void set(String key, Object value) {
        config.put(key, value);
    }

    public void remove(String key) {
        config.remove(key);
    }

    public void clear() {
        config.clear();
    }

    public <T> T get(Class<T> clazz) {
        return ContextUtil.read(config, clazz.getCanonicalName(), clazz, true);
    }

    public void set(Object value) {
        config.put(value.getClass().getCanonicalName(), value);
    }

}
