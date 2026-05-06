package io.hankun.framework.ai.mcp.entity;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @className: ToolKey
 * @createAt: 2025/7/21 13:31
 * @author: hankun
 */
public record ToolKey(String schema, String name) {

    public static final String SPLIT = ":";

    public String buildFullName() {
        if (ObjectUtils.isEmpty(schema)) {
            return name;
        }
        return schema + SPLIT + name;
    }

    public boolean checkTool(String toolName) {
        if (ObjectUtils.isEmpty(toolName)) {
            return false;
        }
        //短名称匹配
        if (toolName.equals(name)) {
            return true;
        }
        //完整名称匹配
        return toolName.equals(buildFullName());
    }

    public static List<ToolKey> of(Collection<String> toolNames) {
        if (CollectionUtils.isEmpty(toolNames)) {
            return List.of();
        }
        List<ToolKey> toolKeys = new ArrayList<>(toolNames.size());
        for (String toolName : toolNames) {
            toolKeys.add(buildFromFullName(toolName));
        }
        return toolKeys;
    }

    public static List<ToolKey> of(String... toolNames) {
        if (toolNames == null || toolNames.length == 0) {
            return List.of();
        }
        List<ToolKey> toolKeys = new ArrayList<>(toolNames.length);
        for (String toolName : toolNames) {
            toolKeys.add(buildFromFullName(toolName));
        }
        return toolKeys;
    }


    public static ToolKey buildFromFullName(String fullName) {
        if (ObjectUtils.isEmpty(fullName)) {
            return null;
        }
        String[] split = fullName.split(SPLIT);
        if (split.length == 1) {
            return new ToolKey("", split[0]);
        }
        return new ToolKey(split[0], split[1]);
    }
}
