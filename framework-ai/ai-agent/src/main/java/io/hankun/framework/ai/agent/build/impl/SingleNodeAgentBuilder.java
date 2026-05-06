package io.hankun.framework.ai.agent.build.impl;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.build.AgentBuilder;
import io.hankun.framework.ai.agent.node.KNodeAction;

/**
 * @description:
 * @className: SingleNodeAgentBuilder
 * @createAt: 2025/12/10 10:04
 * @author: hankun
 */
public abstract class SingleNodeAgentBuilder implements AgentBuilder {

    public record SingleNode(String nodeId, Class<? extends KNodeAction> node) {

    }

    @Override
    public KAgent build(KAgent.Builder builder) {
        SingleNode singleNode = setNode();
        builder.startEdge(singleNode.nodeId())
                .addNode(singleNode.nodeId(), singleNode.node())
                .endEdge(singleNode.nodeId());
        builder = update(builder);
        return builder.build();
    }

    public abstract SingleNode setNode();

    public KAgent.Builder update(KAgent.Builder builder) {
        return builder;
    }
}
