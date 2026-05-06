package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.node.KNodeService;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.mem.AiMemService;
import io.hankun.framework.ai.mem.config.MemSummaryConfig;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: LlmMemSummaryNode
 * @createAt: 2025/12/5 15:56
 * @author: hankun
 */
public abstract class LlmMemSummaryNode extends LlmMemSaveNode {

    protected LlmMemSummaryNode(KNodeService kNodeService, AiMemService aiMemService) {
        super(kNodeService, aiMemService);
    }

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        String tag = inputParams.get("tag", String.class);
        MemSummaryConfig memSummaryConfig = MemSummaryConfig.build(tag);
        String uniqueId = buildUniqueId(contextAccess, inputParams);
        MemSummary summary = new MemSummary(contextAccess, inputParams, this);
        aiMemService.addFact(uniqueId, memSummaryConfig, summary);
        return KNodeResult.ofText(summary.getResultString());
    }
}
