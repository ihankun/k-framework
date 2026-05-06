package io.hankun.framework.ai.agent.entity;

import com.alibaba.fastjson2.JSON;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: DataWithMeta
 * @createAt: 2025/9/30 11:19
 * @author: hankun
 */
public record DataWithMeta(String data, Map<String, Object> meta, String type) {

    public static final String META = "meta";

    public static final String TEXT = "text";

    public static final String DATA = "data";

    @SuppressWarnings("unchecked")
    public <T> T fetchMeta(String key) {
        Object value = meta.get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public String fetchStringMeta(String key) {
        Object value = meta.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static DataWithMeta ofText(String text) {
        return new DataWithMeta(text, Map.of(), TEXT);
    }

    public static DataWithMeta ofMeta(Map<String, Object> meta) {
        return new DataWithMeta("", meta, META);
    }

    public static <T> DataWithMeta ofEntity(T entity) {
        return ofData("", JSON.parseObject(JSON.toJSONBytes(entity)));
    }

    public static DataWithMeta ofData(String data, Map<String, Object> meta) {
        return new DataWithMeta(data, meta, DATA);
    }

    public static DataWithMeta combine(@Nullable Collection<DataWithMeta> dataWithMetaList) {
        if (CollectionUtils.isEmpty(dataWithMetaList)) {
            return DataWithMeta.ofData("", Map.of());
        }
        StringBuilder allText = new StringBuilder();
        Map<String, Object> meta = new HashMap<>();
        for (DataWithMeta dataWithMeta : dataWithMetaList) {
            allText.append(dataWithMeta.data());
            meta.putAll(dataWithMeta.meta());
        }
        return DataWithMeta.ofData(allText.toString(), meta);
    }
}
