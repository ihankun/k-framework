package io.hankun.framework.ai.mcp.app.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: ToolUpdateType
 * @createAt: 2025/8/28 09:38
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum ToolUpdateType {
    REMOVE("remove", "删除"),
    ADD("add", "添加"),
    ;
    private final String type;
    private final String desc;
}
