package io.hankun.framework.ai.agent.call.entity;

import io.hankun.framework.ai.agent.entity.NodeResultData;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: AgentCallResponse
 * @createAt: 2025/12/10 16:10
 * @author: hankun
 */
public record AgentCallResponse(AgentCallKey agentCallKey,
                                Flux<NodeResultData> output) {
}
