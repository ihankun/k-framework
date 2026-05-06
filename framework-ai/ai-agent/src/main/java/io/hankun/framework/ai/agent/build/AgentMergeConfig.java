package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.node.config.ActionConfig;

/**
 * @description:
 * @className: AgentMergeConfig
 * @createAt: 2025/10/27 18:52
 * @author: hankun
 */
public record AgentMergeConfig(String originNodeId, String targetNodeId, ActionConfig newConfig) {
}
