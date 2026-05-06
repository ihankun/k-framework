package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.node.KEdgeAction;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.task.StatusManageService;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: TaskCheckEdgeAction
 * @createAt: 2025/10/30 16:51
 * @author: hankun
 */
@Component
public class TaskCheckEdgeAction implements KEdgeAction {

    private final StatusManageService statusManageService;

    public TaskCheckEdgeAction(StatusManageService statusManageService) {
        this.statusManageService = statusManageService;
    }

    @Override
    public String desc() {
        return "是否新任务判定节点";
    }

    @Override
    public String route(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        CurrentId currentId = getCurrentId();
        return statusManageService.taskInterrupt(currentId.sessionId()) ? "continue" : "new";
    }
}
