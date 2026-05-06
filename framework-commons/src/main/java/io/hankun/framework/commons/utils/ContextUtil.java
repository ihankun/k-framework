package io.hankun.framework.commons.utils;

import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: ContextUtil
 * @createAt: 2025/8/29 08:54
 * @author: hankun
 */
public class ContextUtil {

    @SuppressWarnings("unchecked")
    public static <T> T getData(Map<String, Object> context, String key) {
        Object data = context.get(key);
        if (data == null) {
            return null;
        }
        return (T) data;
    }

    public static <T> void setData(Map<String, Object> context, String key, T data) {
        context.put(key, data);
    }


    @SuppressWarnings("unchecked")
    public static <T> List<T> getDataList(Map<String, Object> context, String key) {
        Object data = context.get(key);
        if (data == null) {
            return null;
        }
        return (List<T>) data;
    }

    public static String readAsString(Map<String, Object> context, String key) {
        return JSON.toJSONString(context.get(key));
    }

    public static <T> T read(Map<String, Object> context, String key, Class<T> clazz, boolean update) {
        Object value = context.get(key);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            T result = JSON.parseObject(JSON.toJSONBytes(value), clazz);
            if (update) {
                context.put(key, result);
            }
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> readList(Map<String, Object> context, String key, Class<T> clazz, boolean update) {
        Object value = context.get(key);
        List<T> result = null;
        if (value instanceof List<?> listValue) {
            if (listValue.isEmpty()) {
                result = (List<T>) listValue;
            } else {
                if (clazz.isInstance(listValue.getFirst())) {
                    result = (List<T>) listValue;
                } else {
                    result = new ArrayList<>();
                    for (Object item : listValue) {
                        result.add(JSON.parseObject(JSON.toJSONBytes(item), clazz));
                    }
                    if (update) {
                        context.put(key, result);
                    }
                }
            }
        } else {
            result = JSON.parseArray(JSON.toJSONBytes(value), clazz);
            if (update) {
                context.put(key, result);
            }
        }
        return result;
    }

    public static String convert(Object value) {
        if (value == null) {
            return null;
        }
        return JSON.toJSONString(value);
    }

    public static <T> T convert(String value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        return JSON.parseObject(value, clazz);
    }
}
