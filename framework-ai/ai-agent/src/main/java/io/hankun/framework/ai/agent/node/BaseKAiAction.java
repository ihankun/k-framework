package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.model.options.KChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: BaseKAiAction
 * @createAt: 2025/12/9 10:02
 * @author: hankun
 */
@Slf4j
public abstract class BaseKAiAction {

    protected final KNodeService kNodeService;

    protected BaseKAiAction(KNodeService kNodeService) {
        this.kNodeService = kNodeService;
    }

    protected LlmCall.Builder buildLlm(@NotNull ContextAccess contextAccess,
                                       @NotNull InputParams inputParams,
                                       @NotNull ActionConfig config,
                                       @NotNull String input) {
        KChatOptions.KChatOptionsBuilder optionsBuilder = KChatOptions.builder();
        optionsBuilder.streamUsage(true);
        optionsBuilder.model(config.getModel());
        setOptions(optionsBuilder, contextAccess);
        List<String> tools = new ArrayList<>();
        if (!CollectionUtils.isEmpty(config.getTools())) {
            tools.addAll(config.getTools());
        }
        return kNodeService.buildNodeLlm(contextAccess, optionsBuilder.build(), tools, input);
    }

    public void setOptions(@NotNull KChatOptions.KChatOptionsBuilder builder,
                           @NotNull ContextAccess contextAccess) {

    }

    protected String buildLlmInput(@NotNull ContextAccess contextAccess,
                                   @NotNull InputParams inputParams) {
        return inputParams.formatInput();
    }
}
