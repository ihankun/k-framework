package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.config.AgentConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.task.StatusManageService;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @className: BaseKNode
 * @createAt: 2025/11/5 14:56
 * @author: hankun
 */
@Slf4j
public abstract class BaseKNode {

    private final ActionConfig baseConfig;

    protected ActionConfig actionConfig;

    protected final StatusManageService statusManageService;

    protected final AgentConfig agentConfig;

    private final KAgent kAgent;

    public BaseKNode(ActionConfig actionConfig,
                     AgentConfig agentConfig,
                     StatusManageService statusManageService,
                     KAgent kAgent) {
        this.actionConfig = actionConfig;
        this.statusManageService = statusManageService;
        this.agentConfig = agentConfig;
        this.baseConfig = actionConfig;
        this.kAgent = kAgent;
    }


    public void freshConfig() {
        actionConfig = ActionConfig.merge(baseConfig, kAgent.getConfigMap().get(baseConfig.getNodeId()));
    }
}
