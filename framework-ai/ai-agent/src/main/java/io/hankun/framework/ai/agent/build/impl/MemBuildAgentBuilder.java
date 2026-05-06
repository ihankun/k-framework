package io.hankun.framework.ai.agent.build.impl;

import com.alibaba.cloud.ai.graph.StateGraph;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.build.AgentBuilder;
import io.hankun.framework.ai.agent.context.MemSummaryContext;
import io.hankun.framework.ai.agent.graph.KeyType;
import io.hankun.framework.ai.agent.node.KNodeAction;
import io.hankun.framework.ai.agent.node.impl.MemBuildRouteEdgeAction;

import java.util.Map;

/**
 * @description:
 * @className: MemBuildAgentBuilder
 * @createAt: 2025/12/5 11:25
 * @author: hankun
 */
public abstract class MemBuildAgentBuilder implements AgentBuilder {

    protected void addMemTag(KAgent.Builder builder, String tag, String nodeId, Class<? extends KNodeAction> nodeAction) {
        builder.addNode(nodeId, nodeAction)
                .addCondition("memRoute", tag, nodeId)
                .endEdge(nodeId);
    }


    @Override
    public KAgent build(KAgent.Builder builder) {
        builder.withDefParam()
                .withDefContexts()
                .context(MemSummaryContext.class)
                .addParam("tag", KeyType.REPLACE)
                .addConditionEdge(StateGraph.START, "memRoute", MemBuildRouteEdgeAction.class,
                        Map.of())
        ;
        builder = update(builder);
        return builder.build();
    }

    public abstract KAgent.Builder update(KAgent.Builder builder);

}
