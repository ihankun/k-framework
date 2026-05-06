package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.context.HumanFeedbackContext;
import io.hankun.framework.ai.agent.context.TaskDataContext;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.node.KEdgeAction;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @description:
 * @className: ExecCheckEdgeNodeAction
 * @createAt: 2025/11/10 08:56
 * @author: hankun
 */
@Component
public class ExecCheckEdgeNodeAction implements KEdgeAction {
    @Override
    public String desc() {
        return "任务是否执行完成判断节点";
    }

    @Override
    public String route(@NotNull ContextAccess contextAccess,
                        @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
        if (taskDataContext == null) {
            nodeOutput.emit("缺失上下文，不支持当前节点");
            return "noContext";
        }
        DataWithMeta preResult = taskDataContext.fetchBeforeOutResult();
        String finish = preResult.fetchStringMeta("finish");
        if (StringUtils.hasText(finish)) {
            return "finish";
        }
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        ActionConfig actionConfig = getActionConfig();
        taskDataContext.interrupt(actionConfig.getNextNodeInfo().fetchNextNode("human"));
        String required = preResult.fetchStringMeta("human");
        if (ObjectUtils.isEmpty(required)) {
            required = preResult.fetchStringMeta("next");
        }
        if (ObjectUtils.isEmpty(required)) {
            required = "继续";
        }
        HumanFeedbackContext.createFeedback(contextAccess, taskDataContext.getBeforeId(), required);
        return "continue";
    }
}
