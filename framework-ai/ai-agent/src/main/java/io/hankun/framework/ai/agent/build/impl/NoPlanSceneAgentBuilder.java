package io.hankun.framework.ai.agent.build.impl;

import com.alibaba.cloud.ai.graph.StateGraph;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.build.AgentBuilder;
import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.context.SceneStepContext;
import io.hankun.framework.ai.agent.node.KContextBuildAction;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.impl.ExecCheckEdgeNodeAction;
import io.hankun.framework.ai.agent.node.impl.ExecChooseEdgeAction;
import io.hankun.framework.ai.agent.node.impl.HumanFeedbackNodeAction;
import io.hankun.framework.ai.agent.node.impl.SceneLLmNodeAction;

import java.util.Map;

/**
 * @description:
 * @className: NoPlanSceneAgentBuilder
 * @createAt: 2025/11/10 08:53
 * @author: hankun
 */
public abstract class NoPlanSceneAgentBuilder implements AgentBuilder {

    @Override
    public KAgent build(KAgent.Builder builder) {
        builder.context(SceneContext.class)
                .context(SceneStepContext.class)
                .addNode("contextSet", KContextBuildAction.class)
                .startEdge("contextSet")
                .addConditionEdge("contextSet", "choose", ExecChooseEdgeAction.class,
                        Map.of("simple", "simpleExec", "complex", "interactionExec"))
                .addNode("simpleExec", SceneLLmNodeAction.class)
                .endEdge("simpleExec")
                .addNode("interactionExec", SceneLLmNodeAction.class)
                .addConfig("interactionExec", ActionConfig.builder().output(true).build())
                .addNode("humanInput", HumanFeedbackNodeAction.class)
                .addConditionEdge("interactionExec", "resultCheck", ExecCheckEdgeNodeAction.class,
                        Map.of("noContext", StateGraph.END, "finish", StateGraph.END, "continue", "humanInput"))
                .interruptBefore("humanInput")
                .addNode("humanContext", KContextBuildAction.class)
                .addEdge("humanInput", "humanContext")
                .addEdge("humanContext", "interactionExec")
        ;
        builder = update(builder);
        return builder.build();
    }

    public abstract KAgent.Builder update(KAgent.Builder builder);
}
