package io.hankun.framework.ai.mcp.callback;

import org.springframework.ai.tool.ToolCallback;

/**
 * @description:
 * @className: KToolCallbackFetchFilter
 * @createAt: 2025/6/5 11:52
 * @author: hankun
 */
public interface KToolCallbackFetchFilter {
    /**
     * 过滤器
     *
     * @param toolCallback callback
     * @return boolean true:通过
     */
    boolean filter(ToolCallback toolCallback);
}
