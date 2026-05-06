package io.hankun.framework.ai.tools.toolcall.intercept;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.core.Ordered;

/**
 * @description:
 * @className: ToolCallingInterceptor
 * @createAt: 2025/10/20 09:04
 * @author: hankun
 */
public interface ToolCallingInterceptor extends Ordered {

    ToolExecutionResult intercept(@NotNull Prompt prompt, @NotNull ChatResponse chatResponse, ToolCallingInterceptChain chain);

}
