package io.hankun.framework.ai.tools.intercept;

import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.mcp.interceptor.ToolInterceptor;
import io.hankun.framework.ai.mcp.app.KHttpToolCallback;
import io.hankun.framework.ai.mcp.app.ToolCallbackBuildService;
import io.hankun.framework.ai.tools.intercept.msun.MsunFunctionInterceptor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: MsunCallsInterceptorManager
 * @createAt: 2025/6/25 14:54
 * @author: hankun
 */
@Component
public class MsunCallsInterceptorManager implements ToolInterceptor {

    private final Map<ToolKey, MsunFunctionInterceptor> msunFunctionInterceptors = new HashMap<>();

    private final ToolCallbackBuildService toolCallbackBuildService;

    public MsunCallsInterceptorManager(List<MsunFunctionInterceptor> msunFunctionInterceptors,
                                       ToolCallbackBuildService toolCallbackBuildService) {
        this.toolCallbackBuildService = toolCallbackBuildService;
        for (MsunFunctionInterceptor msunFunctionInterceptor : msunFunctionInterceptors) {
            this.msunFunctionInterceptors.put(msunFunctionInterceptor.functionName(), msunFunctionInterceptor);
        }
    }

    @Override
    public List<Class<? extends ToolCallback>> interceptorTools() {
        return List.of(KHttpToolCallback.class);
    }

    @Override
    public String intercept(String toolInput, ToolContext tooContext, ToolCallback toolCallback) {
        String toolName = toolCallback.getToolDefinition().name();
        ToolKey toolKey = toolCallbackBuildService.getByShortName(toolName);
        MsunFunctionInterceptor interceptor = msunFunctionInterceptors.get(toolKey);
        if (interceptor != null) {
            return interceptor.intercept(toolInput, tooContext, toolCallback);
        }
        return toolCallback.call(toolInput, tooContext);
    }

    @Override
    public String intercept(String toolInput, ToolCallback toolCallback) {
        return toolCallback.call(toolInput);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
