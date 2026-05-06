package io.hankun.framework.ai.agent;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.checkpoint.BaseCheckpointSaver;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.constant.SaverEnum;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import io.hankun.framework.ai.agent.api.entity.GraphNodeInfo;
import io.hankun.framework.ai.agent.api.entity.GraphParams;
import io.hankun.framework.ai.agent.build.AgentMergeConfig;
import io.hankun.framework.ai.agent.config.AgentConfig;
import io.hankun.framework.ai.agent.config.KNodeConfig;
import io.hankun.framework.ai.agent.context.HumanFeedbackContext;
import io.hankun.framework.ai.agent.context.TaskCommonToolCallContext;
import io.hankun.framework.ai.agent.context.TaskDataContext;
import io.hankun.framework.ai.agent.graph.GraphBuildService;
import io.hankun.framework.ai.agent.graph.KeyType;
import io.hankun.framework.ai.agent.node.*;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.store.history.context.ChatRecordContext;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.history.context.TaskRecordContext;
import io.hankun.framework.ai.tools.client.ClientConfig;
import io.hankun.framework.ai.tools.contexts.TimeContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @description:
 * @className: KAgent
 * @createAt: 2025/10/24 09:05
 * @author: hankun
 */
@Slf4j
public class KAgent {

    @Getter
    private final AgentConfig agentConfig;

    @Getter
    private final Map<String, Class<? extends KNodeAction>> kNodeActionMap;

    @Getter
    private final Map<String, Class<? extends KEdgeAction>> kEdgeActionMap;

    @Getter
    private final Map<String, ActionConfig> configMap;

    @Getter
    private final GraphNodeInfo graphNodeInfo;

    private final KNodeManageService kNodeManageService;

    private final GraphBuildService graphBuildService;

    private volatile CompiledGraph compiledGraph;

    private final StateGraph stateGraph;

    @Getter
    private final Set<String> interruptsBefore;
    @Getter
    private final Set<String> interruptsAfter;
    @Getter
    private final boolean interruptBeforeEdge;

    @Getter
    private final BaseCheckpointSaver checkpointSaver;

    private final String checkpointSaveType;

    @Getter
    private final String plantUML;

    public KAgent(AgentConfig agentConfig, Map<String, Class<? extends KNodeAction>> kNodeActionMap,
                  Map<String, Class<? extends KEdgeAction>> kEdgeActionMap,
                  Map<String, ActionConfig> configMap, GraphNodeInfo graphNodeInfo,
                  KNodeManageService kNodeManageService, GraphBuildService graphBuildService,
                  Set<String> interruptsBefore,
                  Set<String> interruptsAfter, boolean interruptBeforeEdge) {
        this.agentConfig = agentConfig;
        this.kNodeActionMap = kNodeActionMap;
        this.kEdgeActionMap = kEdgeActionMap;
        this.configMap = configMap;
        this.graphNodeInfo = graphNodeInfo;
        this.kNodeManageService = kNodeManageService;
        this.graphBuildService = graphBuildService;
        this.interruptsBefore = interruptsBefore;
        this.interruptsAfter = interruptsAfter;
        this.interruptBeforeEdge = interruptBeforeEdge;
        this.checkpointSaver = new MemorySaver();
        this.checkpointSaveType = SaverEnum.MEMORY.getValue();
        this.stateGraph = build();
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                agentConfig.getAgentCode() + "流程图");
        log.info("\n=== {} UML Flow ===", agentConfig.getAgentCode());
        this.plantUML = representation.content();
        log.info(plantUML);
        log.info("==================================\n");

    }

    public Map<String, GraphParams> getGraphParams() {
        return graphNodeInfo.getParams();
    }

    public String getCode() {
        return agentConfig.getAgentCode();
    }

    public String getDesc() {
        return agentConfig.getAgentDesc();
    }

    private StateGraph build() {
        try {
            Map<String, NodeAction> nodeActionMap = new HashMap<>(kNodeActionMap.size());
            for (Map.Entry<String, Class<? extends KNodeAction>> entry : kNodeActionMap.entrySet()) {
                nodeActionMap.put(entry.getKey(), kNodeManageService.buildNode(configMap.get(entry.getKey()), agentConfig, this, entry.getValue()));
            }
            Map<String, EdgeAction> edgeActionMap = new HashMap<>(kEdgeActionMap.size());
            for (Map.Entry<String, Class<? extends KEdgeAction>> entry : kEdgeActionMap.entrySet()) {
                edgeActionMap.put(entry.getKey(), kNodeManageService.buildEdge(configMap.get(entry.getKey()), agentConfig, this, entry.getValue()));
            }
            return graphBuildService.buildStateGraph(graphNodeInfo, nodeActionMap, edgeActionMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompiledGraph getAndCompile() {
        if (compiledGraph == null) {
            synchronized (this) {
                if (compiledGraph == null) {
                    try {
                        SaverConfig saverConfig = SaverConfig.builder().register(checkpointSaveType, checkpointSaver).build();
                        CompileConfig compileConfig = CompileConfig.builder().saverConfig(saverConfig)
                                .interruptsBefore(interruptsBefore)
                                .interruptsAfter(interruptsAfter)
                                .interruptBeforeEdge(interruptBeforeEdge).build();
                        compiledGraph = stateGraph.compile(compileConfig);
                    } catch (GraphStateException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return compiledGraph;
    }

    public static Builder builder(String code, KNodeManageService kNodeManageService,
                                  GraphBuildService graphBuildService, KNodeConfig nodeConfig) {
        return new Builder(code, kNodeManageService, graphBuildService, nodeConfig);
    }


    public static class Builder {

        @Getter
        private final AgentConfig agentConfig = new AgentConfig();

        private final Map<String, Class<? extends KNodeAction>> kNodeActionMap = new HashMap<>();

        private final Map<String, Class<? extends KEdgeAction>> kEdgeActionMap = new HashMap<>();

        private final Map<String, ActionConfig> configMap = new HashMap<>();

        private final GraphNodeInfo graphNodeInfo = new GraphNodeInfo();

        private final KNodeManageService kNodeManageService;
        private final GraphBuildService graphBuildService;
        private final KNodeConfig kNodeConfig;

        private final Set<String> interruptsBefore = new HashSet<>();

        private final Set<String> interruptsAfter = new HashSet<>();

        private boolean interruptBeforeEdge = false;

        private final Map<String, KAgent> subAgentMap = new HashMap<>();

        private final Map<String, List<AgentMergeConfig>> mergeConfigMap = new HashMap<>();

        public Builder(String code, KNodeManageService kNodeManageService,
                       GraphBuildService graphBuildService, KNodeConfig kNodeConfig) {
            this.kNodeManageService = kNodeManageService;
            this.graphBuildService = graphBuildService;
            this.kNodeConfig = kNodeConfig;
            agentConfig.setAgentCode(code);
        }

        public Builder withDefParam() {
            graphNodeInfo.addParam(NodeUtil.INPUT, KeyType.REPLACE.getKey());
            graphNodeInfo.addParam(NodeUtil.INTENTION, KeyType.REPLACE.getKey(), "");
            graphNodeInfo.addParam(NodeUtil.RESULT, KeyType.APPEND.getKey());
            graphNodeInfo.addParam(NodeUtil.CURRENT_ID, KeyType.REPLACE.getKey());
            return this;
        }

        public Builder withDefContexts() {
            return this.context(TaskDataContext.class)
                    .context(ModelRecordContext.class)
                    .context(ChatRecordContext.class)
                    .context(TaskRecordContext.class)
                    .context(HumanFeedbackContext.class)
                    .context(TimeContext.class)
                    .context(TaskCommonToolCallContext.class)
                    .context(NodeOutput.class)
                    .context(InputParams.class)
                    ;
        }

        public Builder interruptBefore(String interruptBefore) {
            this.interruptsBefore.add(interruptBefore);
            return this;
        }

        public Builder interruptAfter(String interruptAfter) {
            this.interruptsAfter.add(interruptAfter);
            return this;
        }

        public Builder interruptBeforeEdge(boolean interruptBeforeEdge) {
            this.interruptBeforeEdge = interruptBeforeEdge;
            return this;
        }

        public Builder context(Class<? extends IContext> context) {
            agentConfig.getContexts().add(context);
            return this;
        }

        public Builder desc(String desc) {
            agentConfig.setAgentDesc(desc);
            return this;
        }

        public Builder version(String version) {
            agentConfig.setVersion(version);
            return this;
        }

        public Builder maxExecTimeSeconds(Integer maxExecTimeSeconds) {
            agentConfig.setMaxExecTimeSeconds(maxExecTimeSeconds);
            return this;
        }

        public Builder addNode(String nodeId,
                               Class<? extends KNodeAction> kNodeAction) {
            return addNode(nodeId, kNodeAction, null);
        }

        public Builder replaceNodeName(String oldNodeId, String newNodeId) {
            Class<? extends KNodeAction> kNodeAction = kNodeActionMap.get(oldNodeId);
            ActionConfig actionConfig = configMap.get(oldNodeId);
            graphNodeInfo.replaceNode(oldNodeId, newNodeId);
            kNodeActionMap.put(newNodeId, kNodeAction);
            configMap.put(newNodeId, actionConfig);
            return this;
        }

        public Builder addConfig(String nodeId, ActionConfig actionConfig) {
            configMap.compute(nodeId, (k, old) -> ActionConfig.merge(old, actionConfig));
            return this;
        }

        public Builder addConfig(String nodeId, ClientConfig clientConfig) {
            return addConfig(nodeId, ActionConfig.builder().clientConfig(clientConfig).build());
        }

        public Builder addTools(String nodeId, List<String> tools) {
            return addConfig(nodeId, ActionConfig.builder().tools(tools).build());
        }

        public Builder inputFormat(String nodeId, String inputFormat) {
            return addConfig(nodeId, ActionConfig.builder().inputFormat(inputFormat).build());
        }

        public Builder addUserContext(String nodeId, String contextKey) {
            ActionConfig actionConfig = configMap.get(nodeId);
            if (actionConfig != null) {
                actionConfig.getUserContext().add(contextKey);
            }
            return this;
        }

        public Builder addNode(String nodeId,
                               Class<? extends KNodeAction> msunNodeAction,
                               ActionConfig actionConfig) {
            graphNodeInfo.addNode(nodeId);
            kNodeActionMap.put(nodeId, msunNodeAction);
            configMap.put(nodeId, buildConfig(nodeId, actionConfig));
            return this;
        }

        public Builder addSubAgent(String nodeId, KAgent subAgent, List<AgentMergeConfig> mergeConfigs) {
            subAgentMap.put(nodeId, subAgent);
            mergeConfigMap.put(nodeId, mergeConfigs);
            return this;
        }

        public ActionConfig buildConfig(String nodeId, ActionConfig actionConfig) {
            KNodeConfig.NodeConfig nodeConfig = kNodeConfig.loadConfig(nodeId);
            return ActionConfig.merge(ActionConfig.build(nodeId, nodeConfig.getModel(), nodeConfig.getStream()), actionConfig);
        }

        public Builder addEdge(String start, String end) {
            ActionConfig actionConfig = configMap.get(start);
            if (actionConfig != null) {
                actionConfig.getNextNodeInfo().setNextNode(end);
            }
            graphNodeInfo.addEdge(start, end);
            return this;
        }

        public Builder addConditionEdge(String sourceId, String edgeId, Class<? extends KEdgeAction> kEdgeAction, Map<String, String> mappings) {
            return addConditionEdge(sourceId, edgeId, kEdgeAction, null, mappings);
        }

        public Builder addConditionEdge(String sourceId, String edgeId, Class<? extends KEdgeAction> kEdgeAction,
                                        ActionConfig actionConfig,
                                        Map<String, String> mappings) {
            kEdgeActionMap.put(edgeId, kEdgeAction);
            graphNodeInfo.addConditionEdge(sourceId, edgeId, mappings);
            ActionConfig config = buildConfig(edgeId, actionConfig);
            config.getNextNodeInfo().setTargetNodes(mappings);
            configMap.put(edgeId, config);
            return this;
        }

        public Builder addCondition(String edgeId, String edgeResult, String mappingNode) {
            graphNodeInfo.addCondition(edgeId, edgeResult, mappingNode);
            return this;
        }

        public Builder addParam(String paramCode, KeyType type) {
            return addParam(paramCode, type, null);
        }

        public Builder addParam(String paramCode, KeyType type, Object def) {
            graphNodeInfo.addParam(paramCode, type.getKey(), def);
            return this;
        }

        public Builder startEdge(String nodeId) {
            graphNodeInfo.startEdge(nodeId);
            return this;
        }

        public Builder endEdge(String nodeId) {
            graphNodeInfo.endEdge(nodeId);
            return this;
        }


        public KAgent build() {
            for (Map.Entry<String, KAgent> entry : subAgentMap.entrySet()) {
                //获取子节点合并配置
                List<AgentMergeConfig> mergeConfigs = mergeConfigMap.get(entry.getKey());
                if (!CollectionUtils.isEmpty(mergeConfigs)) {
                    for (AgentMergeConfig mergeConfig : mergeConfigs) {
                        ActionConfig oldConfig = entry.getValue().configMap.get(mergeConfig.originNodeId());
                        ActionConfig actionConfig = ActionConfig.merge(oldConfig, mergeConfig.newConfig());
                        configMap.put(mergeConfig.targetNodeId(), actionConfig);
                    }
                }
                GraphNodeInfo subGraphNodeInfo = entry.getValue().graphNodeInfo;
                for (Map.Entry<String, GraphParams> paramEntry : subGraphNodeInfo.getParams().entrySet()) {
                    GraphParams param = paramEntry.getValue();
                    graphNodeInfo.addParam(param.paramKey(), param.paramType(), param.defValue());
                }

            }

            return new KAgent(agentConfig, kNodeActionMap, kEdgeActionMap, configMap, graphNodeInfo,
                    kNodeManageService, graphBuildService,
                    interruptsBefore, interruptsAfter, interruptBeforeEdge);
        }
    }
}
