package io.hankun.framework.ai.agent.node.impl;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.exec.AgentGraph;
import io.hankun.framework.ai.agent.node.KNodeAction;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.agent.task.StatusManageService;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: IntentContinueCheckNodeAction
 * @createAt: 2025/10/31 13:43
 * @author: hankun
 */
@Component
public class IntentContinueCheckNodeAction implements KNodeAction {


    private final StatusManageService statusManageService;

    public IntentContinueCheckNodeAction(StatusManageService statusManageService) {
        this.statusManageService = statusManageService;
    }

    @Override
    public String desc() {
        return "意图识别-继续";
    }

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        String reset = inputParams.get("reset", String.class);
        TaskExecConfig taskExecConfig = TaskExecConfig.of(inputParams);
        AgentGraph agentGraph = taskExecConfig.getAgentGraph();
        if ("true".equals(reset)) {
            CurrentId currentId = getCurrentId();
            KAgent kAgent = agentGraph.getNext(currentId.agentId());
            if (kAgent != null) {
                statusManageService.cancelTask(currentId.sessionId(), kAgent);
            }
        }
        return KNodeResult.ofEmptyResult();
    }
}
