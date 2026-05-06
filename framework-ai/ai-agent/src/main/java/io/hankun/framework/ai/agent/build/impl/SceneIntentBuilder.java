package io.hankun.framework.ai.agent.build.impl;

import com.alibaba.cloud.ai.graph.StateGraph;
import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.build.AgentBuilder;
import io.hankun.framework.ai.agent.context.SceneContext;
import io.hankun.framework.ai.agent.context.SceneStepContext;
import io.hankun.framework.ai.agent.graph.KeyType;
import io.hankun.framework.ai.agent.node.impl.IntentContinueCheckNodeAction;
import io.hankun.framework.ai.agent.node.impl.IntentNodeAction;
import io.hankun.framework.ai.agent.node.impl.TaskCheckEdgeAction;
import io.hankun.framework.ai.tools.advisors.AdvisorConfig;
import io.hankun.framework.ai.tools.client.ClientConfig;

import java.util.Map;

/**
 * @description:
 * @className: SceneIntentBuilder
 * @createAt: 2025/10/30 17:20
 * @author: hankun
 */
public abstract class SceneIntentBuilder implements AgentBuilder {

    public static final String CATEGORY = "category";

    public void withCategory(KAgent.Builder builder, String category) {
        builder.getAgentConfig().getCategories().add(category);
    }

    @Override
    public KAgent build(KAgent.Builder builder) {
        builder.context(SceneContext.class)
                .context(SceneStepContext.class)
                .addParam("reset", KeyType.REPLACE)
                .addConditionEdge(StateGraph.START, "taskCheck", TaskCheckEdgeAction.class,
                        Map.of("continue", "intentContinue", "new", "intent"))
                .addNode("intent", IntentNodeAction.class)
                .addNode("intentContinue", IntentNodeAction.class)
                .addNode("intentCheck", IntentContinueCheckNodeAction.class)
                .addEdge("intentContinue", "intentCheck")
                .endEdge("intent")
                .endEdge("intentCheck")
                .addConfig("intent", new ClientConfig(AdvisorConfig.builder()
                        .withAdvisor("memory")
                        .build()))
                .addConfig("intentContinue", new ClientConfig(AdvisorConfig.builder()
                        .withAdvisor("memory")
                        .build()))
        ;
        builder = update(builder);
        return builder.build();
    }

    public abstract KAgent.Builder update(KAgent.Builder builder);
}
