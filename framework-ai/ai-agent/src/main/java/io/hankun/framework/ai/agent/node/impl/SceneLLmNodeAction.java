package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.context.*;
import io.hankun.framework.ai.agent.llm.LlmCall;
import io.hankun.framework.ai.agent.node.BaseKAiNodeAction;
import io.hankun.framework.ai.agent.node.KNodeService;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.record.RecordHolder;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.model.options.KChatOptions;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.history.detail.TaskToolCallRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @description:
 * @className: SceneLLmNodeAction
 * @createAt: 2025/10/24 10:13
 * @author: hankun
 */
@Component
public class SceneLLmNodeAction extends BaseKAiNodeAction {

    public SceneLLmNodeAction(KNodeService kNodeService) {
        super(kNodeService);
    }

    @Override
    public void setOptions(@NotNull KChatOptions.KChatOptionsBuilder builder, @NotNull ContextAccess contextAccess) {
        builder.parallelToolCalls(true);
        SceneStepContext sceneContext = contextAccess.getData(SceneStepContext.class);
        if (StringUtils.hasText(sceneContext.model())) {
            builder.model(sceneContext.model());
        }
    }

    @Override
    public void beforeLlm(@NotNull LlmCall.Builder builder, @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        SceneContext sceneContext = contextAccess.getData(SceneContext.class);
        SceneStep step = sceneContext.loadCurrentStep();
        TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
        taskDataContext.setBeforeTarget(step.stepTarget());
        taskDataContext.setBeforeOutput(step.output());
        for (String tool : step.tools()) {
            builder.addTool(tool);
        }
    }

    @Override
    public void afterLlm(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput, @NotNull KNodeResult nodeResult) {
        SceneStepContext sceneContext = contextAccess.getData(SceneStepContext.class);
        List<ToolKey> commonTools = ToolKey.of(sceneContext.commonTools());
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        TaskCommonToolCallContext taskCommonToolCallContext = contextAccess.getData(TaskCommonToolCallContext.class);
        CurrentId currentId = getCurrentId();
        for (RecordHolder<TaskToolCallRecord> recordHolder : modelRecordContext.taskToolCallRecords()) {
            TaskToolCallRecord record = recordHolder.record();
            if (record.currentId().equals(currentId)) {
                for (ToolKey commonTool : commonTools) {
                    if (commonTool.checkTool(record.toolName())) {
                        taskCommonToolCallContext.records().add(record);
                    }
                }
            }
        }
    }

    @Override
    protected boolean checkOutput(ContextAccess contextAccess, InputParams inputParams) {
        ActionConfig actionConfig = getActionConfig();
        if (actionConfig.getOutput() != null && actionConfig.getOutput()) {
            return true;
        }
        SceneContext sceneContext = contextAccess.getData(SceneContext.class);
        SceneStep step = sceneContext.loadCurrentStep();
        if (step == null) {
            return false;
        }
        return step.output();
    }

    @Override
    public String desc() {
        return "场景llm节点";
    }
}
