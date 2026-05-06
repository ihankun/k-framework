package io.hankun.framework.ai.mem.entity;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: MemDataVo
 * @createAt: 2025/12/4 16:07
 * @author: hankun
 */
@Data
public class MemDataVo {
    private String id;
    private String content;
    private Map<String, Object> meta;
    private Float score;

    public String buildDesc() {
        Map<String, Object> result = buildMap();
        return JSON.toJSONString(result);
    }

    public Map<String, Object> buildMap() {
        Map<String, Object> result = new HashMap<>(meta);
        result.put("content", content);
        result.put("score", score);
        return result;
    }
}
