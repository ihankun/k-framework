package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.context.MemSummaryContext;
import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.BaseKAiNodeAction;
import io.hankun.framework.ai.agent.node.KNodeService;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.common.util.FluxUtil;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.mem.AiMemService;
import io.hankun.framework.ai.mem.config.MemSummaryConfig;
import io.hankun.framework.ai.mem.entity.MemData;
import io.hankun.framework.ai.mem.summary.SummaryUpdater;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: LlmMemSaveNode
 * @createAt: 2025/12/5 10:16
 * @author: hankun
 */
public abstract class LlmMemSaveNode extends BaseKAiNodeAction {

    protected final AiMemService aiMemService;


    protected LlmMemSaveNode(KNodeService kNodeService,
                             AiMemService aiMemService) {
        super(kNodeService);
        this.aiMemService = aiMemService;
    }

    @Override
    public String desc() {
        return "记忆写入";
    }

    public abstract String buildUniqueId(ContextAccess contextAccess, InputParams inputParams);

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        String tag = inputParams.get("tag", String.class);
        Map<String, Object> meta = (Map<String, Object>) inputParams.get("meta", Map.class);
        String content = inputParams.get("content", String.class);
        if (ObjectUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("tag不能为空");
        }
        MemSummaryConfig memSummaryConfig = MemSummaryConfig.build(tag);
        String uniqueId = buildUniqueId(contextAccess, inputParams);
        MemSummary summary = new MemSummary(contextAccess, inputParams, this);
        aiMemService.addFact(uniqueId, content, memSummaryConfig, meta, summary);
        return KNodeResult.ofText(summary.getResultString());
    }

    @Override
    public void beforeLlm(@NotNull LlmCall.Builder builder, @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        MemSummaryContext memSummaryContext = contextAccess.getData(MemSummaryContext.class);
        builder.addParam("before", buildBefore(memSummaryContext.beforeSummary()));
        builder.addParam("history", buildHistory(memSummaryContext.history()));
        builder.addParam("fact", buildFact(memSummaryContext.facts()));
    }


    public static class MemSummary implements SummaryUpdater {

        private final ContextAccess contextAccess;

        private final InputParams inputParams;

        private final BaseKAiNodeAction node;

        @Getter
        private String resultString;

        public MemSummary(ContextAccess contextAccess, InputParams inputParams, BaseKAiNodeAction node) {
            this.contextAccess = contextAccess;
            this.inputParams = inputParams;
            this.node = node;
        }

        @Override
        public MemData updateSummary(List<MemData> facts, List<MemData> history, MemData beforeSummary) {
            contextAccess.setData(new MemSummaryContext(facts, history, beforeSummary));
            LlmCall call = node.buildCall(contextAccess, inputParams);
            Flux<String> result = call.callWithText();
            resultString = FluxUtil.collectToString(result);
            for (MemData fact : facts) {
                beforeSummary.meta().getExtend().putAll(fact.meta().getExtend());
            }
            contextAccess.removeData(MemSummaryContext.class);
            return new MemData(beforeSummary.id(), resultString, beforeSummary.meta());
        }
    }

    public String buildBefore(MemData before) {
        return "### 原汇总的数据如下：\n" + "    " + before.buildDesc();
    }

    public String buildHistory(List<MemData> history) {
        StringBuilder stringBuilder = new StringBuilder("### 历史数据如下：");
        if (CollectionUtils.isEmpty(history)) {
            return "\n    无历史记忆";
        }
        for (MemData data : history) {
            stringBuilder.append("\n  - ").append(data.buildDesc());
        }
        return stringBuilder.toString();
    }

    public String buildFact(List<MemData> facts) {
        StringBuilder stringBuilder = new StringBuilder("### 新增数据如下：");
        if (CollectionUtils.isEmpty(facts)) {
            return "\n    无新增记忆";
        }
        for (MemData data : facts) {
            stringBuilder.append("\n  - ").append(data.buildDesc());
        }
        return stringBuilder.toString();
    }
}
