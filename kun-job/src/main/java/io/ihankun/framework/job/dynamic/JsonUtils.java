package io.ihankun.framework.job.dynamic;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;

/**
 * @author hankun
 */
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toString(Object obj) {
        return toJson(obj);
    }

    public static String toJson(Object obj) {
        try {
            StringWriter writer = new StringWriter();
            MAPPER.writeValue(writer, obj);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("序列化对象【" + obj + "】时出错", e);
        }
    }

    public static <T> T toBean(Class<T> entityClass, String jsonString) {
        try {
            return MAPPER.readValue(jsonString, entityClass);
        } catch (Exception e) {
            throw new RuntimeException("JSON【" + jsonString + "】转对象时出错", e);
        }
    }

    /**
     * 用于对象通过其他工具已转为JSON的字符形式，这里不需要再加上引号
     *
     * @param obj 数据
     */
    public static String getJsonSuccess(String obj) {
        String jsonString;
        if (obj == null) {
            jsonString = "{\"success\":true}";
        } else {
            jsonString = "{\"success\":true,\"data\":" + obj + "}";
        }
        return jsonString;
    }

    public static String getJsonSuccess(Object obj) {
        return getJsonSuccess(obj, null);
    }

    public static String getJsonSuccess(Object obj, String message) {
        if (obj == null) {
            return "{\"success\":true,\"message\":\"" + message + "\"}";
        } else {
            try {
                return "{\"success\":true," + toString(obj) + ",\"message\":\"" + message + "\"}";
            } catch (Exception e) {
                throw new RuntimeException("序列化对象【" + obj + "】时出错", e);
            }
        }
    }

    public static String getJsonError(Object obj) {
        return getJsonError(obj, null);
    }

    public static String getJsonError(Object obj, String message) {
        if (obj == null) {
            return "{\"success\":false,\"message\":\"" + message + "\"}";
        } else {
            try {
                obj = parseIfException(obj);
                return "{\"success\":false,\"data\":" + toString(obj) + ",\"message\":\"" + message + "\"}";
            } catch (Exception e) {
                throw new RuntimeException("序列化对象【" + obj + "】时出错", e);
            }
        }
    }

    public static Object parseIfException(Object obj) {
        if (obj instanceof Exception) {
            return null;
        }
        return obj;
    }

    public static String getErrorMessage(String defaultMessage) {
        return defaultMessage;
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
