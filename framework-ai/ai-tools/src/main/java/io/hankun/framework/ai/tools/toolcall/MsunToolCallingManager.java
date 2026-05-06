package io.hankun.framework.ai.tools.toolcall;

import io.hankun.framework.ai.tools.toolcall.intercept.ToolCallingInterceptChain;
import io.hankun.framework.ai.tools.toolcall.intercept.ToolCallingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @description:
 * @className: MsunToolCallingManager
 * @createAt: 2025/10/20 08:58
 * @author: hankun
 */
public class MsunToolCallingManager implements ToolCallingManager {

    private final ToolCallingManager toolCallingManager;

    private final List<ToolCallingInterceptor> toolCallingInterceptors;

    public MsunToolCallingManager(ToolCallingManager toolCallingManager,
                                  List<ToolCallingInterceptor> toolCallingInterceptors) {
        this.toolCallingManager = toolCallingManager;
        this.toolCallingInterceptors = new ArrayList<>(toolCallingInterceptors);
        this.toolCallingInterceptors.sort(Comparator.comparingInt(Ordered::getOrder));
    }

    @NotNull
    @Override
    public List<ToolDefinition> resolveToolDefinitions(@NotNull ToolCallingChatOptions chatOptions) {
        return toolCallingManager.resolveToolDefinitions(chatOptions);
    }

    @NotNull
    @Override
    public ToolExecutionResult executeToolCalls(@NotNull Prompt prompt, @NotNull ChatResponse chatResponse) {
        ToolCallingInterceptChain chain = new ToolCallingInterceptChain(toolCallingInterceptors, this, 0);
        return chain.call(prompt, chatResponse);
    }

    public ToolExecutionResult call(Prompt prompt, ChatResponse chatResponse) {
        return toolCallingManager.executeToolCalls(prompt, chatResponse);
    }


}
