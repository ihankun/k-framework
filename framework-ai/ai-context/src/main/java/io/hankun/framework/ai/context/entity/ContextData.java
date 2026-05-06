package io.hankun.framework.ai.context.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import io.hankun.framework.commons.utils.ContextUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @className: ContextData
 * @createAt: 2025/10/23 19:14
 * @author: hankun
 */
public record ContextData(Map<String, Object> data, Map<String, Object> cache) {

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> clazz) {
        return (T) cache.computeIfAbsent(key, k -> {
            Object value = data.get(k);
            if (value == null) {
                return null;
            }
            if (clazz.isInstance(value)) {
                return value;
            }
            if (value instanceof String stringValue) {
                return JSON.parseObject(stringValue, clazz);
            }
            return JSON.parseObject(JSON.toJSONBytes(value), clazz);
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, TypeReference<T> clazz) {
        return (T) cache.computeIfAbsent(key, k -> {
            Object value = data.get(k);
            if (value == null) {
                return null;
            }
            if (value instanceof String stringValue) {
                return clazz.parseObject(stringValue);
            }
            return clazz.parseObject(JSON.toJSONBytes(value));
        });
    }

    public void setData(String key, Object value) {
        cache.put(key, value);
    }

    public void removeData(String key) {
        //data.remove(key);
        cache.remove(key);
    }

    public String readAsString(String key) {
        return ContextUtil.readAsString(cache, key);
    }


    public static ContextData ofMap(Map<String, Object> data) {
        return new ContextData(Map.copyOf(data), new ConcurrentHashMap<>());
    }

    @SuppressWarnings("unchecked")
    public static ContextData of(String json) {
        return new ContextData(JSON.parseObject(json, Map.class), new ConcurrentHashMap<>());
    }

    public static ContextData ofCache(Map<String, Object> data) {
        if (data == null) {
            return of();
        }
        //移除data中的null
        Map<String, Object> cache = new ConcurrentHashMap<>(data.size());
        data.forEach((k, v) -> {
            if (v != null) {
                cache.put(k, v);
            }
        });
        return new ContextData(Map.of(), cache);
    }

    public static ContextData of() {
        return new ContextData(Map.of(), new ConcurrentHashMap<>());
    }

    public JSONObject toJson() {
        return new JSONObject(cache);
    }

    public String toJsonString() {
        return toJson().toJSONString();
    }
}
