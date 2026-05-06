package io.hankun.framework.ai.mcp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: McpToolType
 * @createAt: 2025/6/5 09:56
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum McpToolType {
    MSUN_HTTP("msun_http", "云健康http接口"),
    MCP_NACOS("mcp_nacos", "通过nacos注册的mcp-server"),
    MCP_WEB("mcp_web", "通过web注册的mcp-server"),
    METHOD_CALL("method_call", "本地方法调用"),
    ;
    private final String code;
    private final String desc;
}
