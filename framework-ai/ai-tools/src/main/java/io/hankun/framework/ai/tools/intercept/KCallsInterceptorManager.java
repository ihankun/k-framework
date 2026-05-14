package io.hankun.framework.ai.tools.intercept;

import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.mcp.interceptor.ToolInterceptor;
import io.hankun.framework.ai.mcp.app.KHttpToolCallback;
import io.hankun.framework.ai.mcp.app.ToolCallbackBuildService;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KCallsInterceptorManager
 * @createAt: 2025/6/25 14:54
 * @author: hankun
 */
@Component
public class KCallsInterceptorManager implements ToolInterceptor {

    private final Map<ToolKey, KFunctionInterceptor> functionInterceptors = new HashMap<>();

    private final ToolCallbackBuildService toolCallbackBuildService;

    public KCallsInterceptorManager(List<KFunctionInterceptor> kFunctionInterceptors,
                                    ToolCallbackBuildService toolCallbackBuildService) {
        this.toolCallbackBuildService = toolCallbackBuildService;
        for (KFunctionInterceptor kFunctionInterceptor : kFunctionInterceptors) {
            this.functionInterceptors.put(kFunctionInterceptor.functionName(), kFunctionInterceptor);
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
        KFunctionInterceptor interceptor = functionInterceptors.get(toolKey);
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
