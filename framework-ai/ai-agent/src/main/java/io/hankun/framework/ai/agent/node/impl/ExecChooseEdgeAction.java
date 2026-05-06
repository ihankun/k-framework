package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.context.SceneStepContext;
import io.hankun.framework.ai.agent.node.KEdgeAction;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: ExecChooseEdgeAction
 * @createAt: 2025/10/24 11:14
 * @author: hankun
 */
@Component
public class ExecChooseEdgeAction implements KEdgeAction {

    @Override
    public String desc() {
        return "任务复杂性分类节点";
    }

    @Override
    public String route(@NotNull ContextAccess contextAccess,
                        @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        SceneStepContext sceneContext = contextAccess.getData(SceneStepContext.class);
        if (sceneContext.isSample()) {
            return "simple";
        }
        return "complex";
    }
}
