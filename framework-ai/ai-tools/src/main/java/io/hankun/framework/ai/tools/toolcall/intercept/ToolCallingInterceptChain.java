package io.hankun.framework.ai.tools.toolcall.intercept;

import io.hankun.framework.ai.tools.toolcall.KToolCallingManager;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolExecutionResult;

import java.util.List;

/**
 * @description:
 * @className: ToolCallingInterceptChain
 * @createAt: 2025/10/20 09:08
 * @author: hankun
 */
public class ToolCallingInterceptChain {
    private final List<ToolCallingInterceptor> interceptors;

    private final KToolCallingManager manager;

    private final int index;

    public ToolCallingInterceptChain(List<ToolCallingInterceptor> interceptors,
                                     KToolCallingManager manager, int index) {
        this.interceptors = interceptors;
        this.manager = manager;
        this.index = index;
    }

    public ToolExecutionResult call(Prompt prompt, ChatResponse chatResponse) {
        if (index < interceptors.size()) {
            ToolCallingInterceptor interceptor = interceptors.get(index);
            return interceptor.intercept(prompt, chatResponse, new ToolCallingInterceptChain(interceptors, manager, index + 1));
        }
        return manager.call(prompt, chatResponse);
    }
}
