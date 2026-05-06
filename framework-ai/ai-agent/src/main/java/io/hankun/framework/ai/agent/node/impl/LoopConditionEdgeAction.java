package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.context.TaskDataContext;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.node.KEdgeAction;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @description:
 * @className: LoopConditionEdgeAction
 * @createAt: 2025/10/24 11:12
 * @author: hankun
 */
@Component
public class LoopConditionEdgeAction implements KEdgeAction {

    @Override
    public String desc() {
        return "循环判断节点";
    }

    @Override
    public String route(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        SceneContext sceneContext = contextAccess.getData(SceneContext.class);
        TaskDataContext taskDataContext = contextAccess.getData(TaskDataContext.class);
        DataWithMeta preResult = taskDataContext.fetchBeforeOutResult();
        String fatal = preResult.fetchStringMeta("fatal");
        if (StringUtils.hasText(fatal)) {
            //前置节点返回fatal内容，返回fatal
            nodeOutput.emit(fatal);
            return "fatal";
        }
        if (sceneContext.hasStep()) {
            return "continue";
        }
        return "finish";
    }
}
