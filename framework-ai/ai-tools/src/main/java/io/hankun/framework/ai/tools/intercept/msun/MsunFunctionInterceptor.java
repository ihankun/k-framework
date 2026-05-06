package io.hankun.framework.ai.tools.intercept.msun;

import io.hankun.framework.ai.mcp.entity.ToolKey;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;

/**
 * @description:
 * @className: MsunFunctionInterceptor
 * @createAt: 2025/6/25 14:58
 * @author: hankun
 */
public interface MsunFunctionInterceptor {

    ToolKey functionName();

    String intercept(String toolInput, ToolContext tooContext, ToolCallback toolCallback);
}
