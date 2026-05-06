package io.hankun.framework.ai.agent.call.entity;

import io.hankun.framework.ai.agent.entity.NodeResultData;

/**
 * @description:
 * @className: FluxAgentResponse
 * @createAt: 2025/12/11 16:17
 * @author: hankun
 */
public record FluxAgentResponse(AgentCallKey key, NodeResultData data) {
}
