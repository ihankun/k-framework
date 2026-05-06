package io.hankun.framework.ai.tools.trace.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: DetailType
 * @createAt: 2025/6/30 09:24
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum DetailType {

    EMBEDDING("embedding", "向量化"),

    RE_RANK("reRank", "重排序"),

    CHAT_CALL("chatCall", "chat调用"),

    MCP_CALL("mcpCall", "mcp调用"),
    ;

    private final String type;
    private final String desc;

}
