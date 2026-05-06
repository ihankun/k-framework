package io.hankun.framework.ai.agent.build.impl;

import com.alibaba.cloud.ai.graph.StateGraph;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.build.AgentBuilder;
import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.context.SceneStepContext;
import io.hankun.framework.ai.agent.node.KContextBuildAction;
import io.hankun.framework.ai.agent.node.impl.*;

import java.util.Map;

/**
 * @description:
 * @className: SceneAgentBuilder
 * @createAt: 2025/10/24 11:48
 * @author: hankun
 */

public abstract class SceneAgentBuilder implements AgentBuilder {

    @Override
    public KAgent build(KAgent.Builder builder) {
        builder.context(SceneContext.class)
                .context(SceneStepContext.class)
                .addNode("contextSet", KContextBuildAction.class)
                .startEdge("contextSet")
                .addConditionEdge("contextSet", "choose", ExecChooseEdgeAction.class,
                        Map.of("simple", "simpleExec", "complex", "plan"))
                .addNode("simpleExec", SceneLLmNodeAction.class)
                .endEdge("simpleExec")
                .addNode("plan", PlanNodeAction.class)
                .addNode("complexExec", SceneLLmNodeAction.class)
//                .addConditionEdge("simpleExec", "simpleCheck", ResultAnalyzeEdgeAction.class,
//                        Map.of("success", StateGraph.END, "retry", "simpleExec", "fail", StateGraph.END))
                .addConditionEdge("plan", "loopCheck", LoopConditionEdgeAction.class,
                        Map.of("finish", StateGraph.END, "continue", "complexExec", "fatal", StateGraph.END))
                .addNode("humanInput", HumanFeedbackNodeAction.class)
                .interruptBefore("humanInput")
                .addConditionEdge("complexExec", "complexCheck", ResultAnalyzeEdgeAction.class,
                        Map.of("success", "plan", "failed", "plan", "rewrite", "plan", "human", "humanInput"))
                .addNode("humanContext", KContextBuildAction.class)
                .addEdge("humanInput", "humanContext")
                .addEdge("humanContext", "complexExec")
        ;
        builder = update(builder);
        return builder.build();
    }

    public abstract KAgent.Builder update(KAgent.Builder builder);
}
