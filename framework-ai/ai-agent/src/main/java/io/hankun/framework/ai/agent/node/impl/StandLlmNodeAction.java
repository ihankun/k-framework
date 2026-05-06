package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.node.BaseKAiNodeAction;
import io.hankun.framework.ai.agent.node.KNodeService;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.model.options.KChatOptions;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: StandLlmNodeAction
 * @createAt: 2025/11/5 18:50
 * @author: hankun
 */
@Component
public class StandLlmNodeAction extends BaseKAiNodeAction {

    protected StandLlmNodeAction(KNodeService kNodeService) {
        super(kNodeService);
    }

    @Override
    public void setOptions(@NotNull KChatOptions.KChatOptionsBuilder builder, @NotNull ContextAccess contextAccess) {
        builder.parallelToolCalls(true);
    }


    @Override
    public String desc() {
        return "标准LLM节点";
    }
}
