package io.hankun.framework.ai.scene.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: SceneCallbackPoint
 * @createAt: 2025/9/5 14:54
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum SceneCallbackPoint {
    END("end", "结束"),
    START("start", "开始"),
    BEFORE_MCP("beforeMcp", "MCP之前"),
    AFTER_MCP("afterMcp", "MCP之后"),
    ;

    private final String code;
    private final String desc;
}
