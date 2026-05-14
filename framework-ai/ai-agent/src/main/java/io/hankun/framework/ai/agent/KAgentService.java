package io.hankun.framework.ai.agent;

import io.hankun.framework.ai.agent.api.entity.*;
import io.hankun.framework.ai.agent.build.AgentLoadService;
import io.hankun.framework.ai.agent.context.LoadingContextRegister;
import io.hankun.framework.ai.agent.exec.AgentExecService;
import io.hankun.framework.ai.agent.exec.KAgentsExecutor;
import io.hankun.framework.ai.agent.node.ActionConfigConvertor;
import io.hankun.framework.ai.agent.node.KEdgeAction;
import io.hankun.framework.ai.agent.node.KNodeAction;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.core.config.KCommConfig;
import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.util.ClassUtil;
import io.hankun.framework.ai.context.ContextManageService;
import io.hankun.framework.ai.context.register.ContextRegister;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KAgentService
 * @createAt: 2025/10/24 09:33
 * @author: hankun
 */
@Component
public class KAgentService {


    private final AgentExecService agentExecService;

    private final KCommConfig kCommConfig;

    private final ContextManageService contextManageService;

    private final AgentLoadService agentLoadService;

    public KAgentService(AgentExecService agentExecService,
                         KCommConfig kCommConfig,
                         ContextManageService contextManageService,
                         AgentLoadService agentLoadService) {
        this.agentExecService = agentExecService;
        this.kCommConfig = kCommConfig;
        this.contextManageService = contextManageService;
        this.agentLoadService = agentLoadService;
    }

    public KAgent getKAgent(String agentCode) {
        KAgent agent = agentLoadService.getAgent(agentCode);
        if (agent == null) {
            throw new IllegalArgumentException("未找到对应的Agent");
        }
        return agent;
    }

    public KAgentsExecutor creteAgentExecutor(KAgent kAgent) {
        return agentExecService.creteAgentExecutor(kAgent);
    }

    public KAgentsExecutor creteAgentExecutor(List<String> agents) {
        return agentExecService.creteAgentExecutor(agents.stream().map(this::getKAgent).toList());
    }

    public List<AgentConfigData> listAllAgentConfig() {
        return listAllAgentConfig(null);
    }

    public List<AgentConfigData> listAllAgentConfig(String loadCode) {
        List<AgentDesc> agentDescList = agentLoadService.listAllAgentDesc(loadCode);
        List<AgentConfigData> agentConfigDataList = new ArrayList<>(agentDescList.size());
        for (AgentDesc agentDesc : agentDescList) {
            KAgent kAgent = agentLoadService.getAgent(agentDesc.agentCode(), loadCode);
            AgentConfigData agentConfigData = buildConfig(kAgent);
            agentConfigDataList.add(agentConfigData);
        }
        return agentConfigDataList;
    }


    public AgentConfigData buildConfig(KAgent kAgent) {
        AgentConfigData agentConfigData = getConfigData(kAgent);
        for (Map.Entry<String, ActionConfig> entry : kAgent.getConfigMap().entrySet()) {
            ActionConfig actionConfig = entry.getValue();
            NodeConfigInfo nodeConfigInfo = ActionConfigConvertor.convertConfigInfo(actionConfig);
            agentConfigData.getConfigs().add(nodeConfigInfo);
            PromptDataInfo promptDataInfo = new PromptDataInfo();
            promptDataInfo.setPromptCode(promptDataInfo.getPromptCode());
            agentConfigData.getPrompts().add(promptDataInfo);
        }
        Map<Class<? extends IContext>, ContextRegister<? extends IContext>> contextMap =
                contextManageService.getContextMap(kAgent.getAgentConfig().getContexts());
        for (Map.Entry<Class<? extends IContext>, ContextRegister<? extends IContext>> entry : contextMap.entrySet()) {
            if (entry.getValue() instanceof LoadingContextRegister<? extends IContext> register) {
                if (entry.getValue().registerTime().contextBuild()) {
                    PromptDataInfo data = new PromptDataInfo();
                    data.setPromptCode(register.promptKey());
                    data.setPromptDesc("");
                    data.setPromptData("");
                    data.setParams(List.of());
                    data.setParamsDatas(List.of());
                    agentConfigData.getPrompts().add(data);
                }
            }
        }
        return agentConfigData;
    }

    @NotNull
    private AgentConfigData getConfigData(KAgent kAgent) {
        AgentConfigData agentConfigData = new AgentConfigData();
        agentConfigData.setServiceCode(kCommConfig.getServiceName());
        agentConfigData.setAgentCode(kAgent.getCode());
        agentConfigData.setAgentDesc(kAgent.getDesc());
        agentConfigData.setCategories(kAgent.getAgentConfig().getCategories());
        agentConfigData.setAgentPlantUML(kAgent.getPlantUML());
        agentConfigData.setGraphData(buildGraphData(kAgent));
        agentConfigData.setConfigs(new ArrayList<>());
        agentConfigData.setPrompts(new ArrayList<>());
        agentConfigData.setVersion(kAgent.getAgentConfig().getVersion());
        return agentConfigData;
    }

    public GraphData buildGraphData(KAgent kAgent) {
        GraphNodeInfo graphNodeInfo = kAgent.getGraphNodeInfo();
        GraphData graphData = GraphData.of(kAgent.getInterruptsBefore(), kAgent.getInterruptsAfter(), kAgent.isInterruptBeforeEdge());
        graphData.params().addAll(graphNodeInfo.getParams().values());
        List<GraphData.Node> nodes = new ArrayList<>(graphData.nodes().size());
        for (String node : graphNodeInfo.getNodes()) {
            Class<? extends KNodeAction> nodeType = kAgent.getKNodeActionMap().get(node);
            if (nodeType == null) {
                throw new IllegalArgumentException("未找到对应的NodeAction");
            }
            nodes.add(new GraphData.Node(node, ClassUtil.classToString(nodeType)));
        }
        graphData.nodes().addAll(nodes);
        for (Map.Entry<String, String> entry : graphNodeInfo.getEdges().entrySet()) {
            graphData.edges().add(new GraphData.Edge(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, GraphNodeInfo.ConditionEdge> entry : graphNodeInfo.getConditionEdges().entrySet()) {
            List<GraphData.ConditionMapping> mappings = new ArrayList<>();
            for (Map.Entry<String, String> mapping : entry.getValue().mappings().entrySet()) {
                mappings.add(new GraphData.ConditionMapping(mapping.getKey(), mapping.getValue()));
            }
            Class<? extends KEdgeAction> edgeType = kAgent.getKEdgeActionMap().get(entry.getValue().edgeId());
            if (edgeType == null) {
                throw new IllegalArgumentException("未找到对应的EdgeAction");
            }
            graphData.conditionEdges().add(new GraphData.Condition(entry.getKey(), entry.getValue().edgeId(),
                    ClassUtil.classToString(edgeType), mappings));
        }
        return graphData;
    }
}
