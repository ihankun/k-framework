package io.hankun.framework.ai.mem.entity;

import com.alibaba.fastjson2.JSON;

import java.util.Map;

/**
 * @description:
 * @className: MemData
 * @createAt: 2025/12/4 11:12
 * @author: hankun
 */
public record MemData(String id, String context, MemMeta meta) {


    public String buildDesc() {
        Map<String, Object> map = meta.buildMap();
        map.put("context", context);
        return JSON.toJSONString(map);
    }
}
