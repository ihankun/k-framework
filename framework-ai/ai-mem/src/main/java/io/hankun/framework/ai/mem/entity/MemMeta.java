package io.hankun.framework.ai.mem.entity;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: MemMeta
 * @createAt: 2025/12/4 14:09
 * @author: hankun
 */
@Data
public class MemMeta {
    private String uniqueId;
    private Date createTime;
    private Date updateTime;
    private String tag;
    private String type;
    private Map<String, Object> extend;


    public static MemMeta build(String uniqueId, String tag, String type, Map<String, Object> meta, Date createTime) {
        MemMeta memMeta = new MemMeta();
        memMeta.setUniqueId(uniqueId);
        memMeta.setCreateTime(createTime);
        memMeta.setUpdateTime(null);
        memMeta.setTag(tag);
        memMeta.setType(type);
        memMeta.setExtend(meta);
        return memMeta;
    }


    public Map<String, Object> buildMap() {
        Map<String, Object> result = new HashMap<>(extend);
        result.put("uniqueId", uniqueId);
        result.put("createTime", createTime);
        if (updateTime != null) {
            result.put("updateTime", updateTime);
        }
        result.put("tag", tag);
        result.put("type", type);
        return result;
    }
}
