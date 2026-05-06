package io.hankun.framework.ai.mcp.interceptor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: ToolCallbackProxy
 * @createAt: 2025/6/5 13:51
 * @author: hankun
 */
public class ToolCallbackProxy implements ToolCallback {

    private final ToolCallback toolCallback;

    private final ToolInterceptor interceptor;


    public ToolCallbackProxy(ToolCallback toolCallback,
                             ToolInterceptor interceptor) {
        this.toolCallback = toolCallback;
        this.interceptor = interceptor;
    }

    public ToolCallback getRealToolCallback() {
        if (toolCallback instanceof ToolCallbackProxy toolCallbackProxy) {
            return toolCallbackProxy.getRealToolCallback();
        }
        return toolCallback;
    }

    public ToolCallback mutate(ToolCallback realToolCallback) {
        List<ToolInterceptor> interceptors = new ArrayList<>();
        getInterceptors(interceptors);
        ToolCallback result = realToolCallback;
        for (ToolInterceptor interceptor : interceptors) {
            result = new ToolCallbackProxy(result, interceptor);
        }
        return result;
    }

    private void getInterceptors(List<ToolInterceptor> interceptors) {
        if (toolCallback instanceof ToolCallbackProxy toolCallbackProxy) {
            toolCallbackProxy.getInterceptors(interceptors);
        }
        interceptors.add(interceptor);
    }

    @NotNull
    @Override
    public ToolDefinition getToolDefinition() {
        return toolCallback.getToolDefinition();
    }

    @NotNull
    @Override
    public ToolMetadata getToolMetadata() {
        return toolCallback.getToolMetadata();
    }

    @NotNull
    @Override
    public String call(@NotNull String toolInput) {
        return interceptor.intercept(toolInput, toolCallback);
    }

    @NotNull
    @Override
    public String call(@NotNull String toolInput, ToolContext tooContext) {
        return interceptor.intercept(toolInput, tooContext, toolCallback);
    }
}
