package io.hankun.framework.ai.mcp.callback;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

/**
 * @description:
 * @className: ToolCallbackListProvider
 * @createAt: 2025/6/5 11:58
 * @author: hankun
 */
public class ToolCallbackListProvider implements ToolCallbackProvider {

    private final ToolCallback[] toolCallbacks;

    public ToolCallbackListProvider(ToolCallback... toolCallbacks) {
        this.toolCallbacks = toolCallbacks;
    }

    @NotNull
    @Override
    public ToolCallback[] getToolCallbacks() {
        return toolCallbacks;
    }
}
