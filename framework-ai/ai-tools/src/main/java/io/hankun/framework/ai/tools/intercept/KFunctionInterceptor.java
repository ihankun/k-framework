package io.hankun.framework.ai.tools.intercept;

import io.hankun.framework.ai.mcp.entity.ToolKey;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;

/**
 * @description:
 * @className: KFunctionInterceptor
 * @createAt: 2025/6/25 14:58
 * @author: hankun
 */
public interface KFunctionInterceptor {

    ToolKey functionName();

    String intercept(String toolInput, ToolContext tooContext, ToolCallback toolCallback);
}
