package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.api.convertor.GraphDataConvertor;
import io.hankun.framework.ai.agent.api.entity.*;
import io.hankun.framework.ai.agent.config.KNodeConfig;
import io.hankun.framework.ai.agent.graph.GraphBuildService;
import io.hankun.framework.ai.agent.graph.KeyType;
import io.hankun.framework.ai.agent.node.*;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.common.util.ClassUtil;
import io.hankun.framework.ai.context.ContextService;
import io.hankun.framework.ai.context.model.ModelContextManageService;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @description:
 * @className: AgentStructService
 * @createAt: 2025/11/27 10:16
 * @author: hankun
 */
@Component
public class AgentStructService {

    private final KNodeManageService kNodeManageService;

    private final GraphBuildService graphBuildService;

    private final KNodeConfig kNodeConfig;

    private final ContextService contextService;

    private final NodeConfigService nodeConfigService;

    private final ModelContextManageService modelContextManageService;

    public AgentStructService(KNodeManageService kNodeManageService,
                              GraphBuildService graphBuildService,
                              KNodeConfig kNodeConfig,
                              ContextService contextService,
                              NodeConfigService nodeConfigService,
                              ModelContextManageService modelContextManageService) {
        this.kNodeManageService = kNodeManageService;
        this.graphBuildService = graphBuildService;
        this.kNodeConfig = kNodeConfig;
        this.contextService = contextService;
        this.nodeConfigService = nodeConfigService;
        this.modelContextManageService = modelContextManageService;
    }

    @SuppressWarnings("unchecked")
    public KAgent build(AgentStructInfo agentStructInfo) {
        KAgent.Builder builder = KAgent.builder(agentStructInfo.getAgentCode(), kNodeManageService, graphBuildService, kNodeConfig);
        for (String context : agentStructInfo.getContexts()) {
            builder.context(contextService.getContextClass(context));
        }
        builder.withDefContexts();
        builder.withDefParam();
        builder.desc(agentStructInfo.getAgentDesc());
        builder.version(agentStructInfo.getVersion());
        GraphData graphData = agentStructInfo.getGraphData();
        for (String interruptBefore : graphData.interruptsBefore()) {
            builder.interruptBefore(interruptBefore);
        }
        for (String interruptAfter : graphData.interruptsAfter()) {
            builder.interruptAfter(interruptAfter);
        }
        if (graphData.interruptBeforeEdge() != null) {
            builder.interruptBeforeEdge(graphData.interruptBeforeEdge());
        }
        GraphNodeInfo graphNodeInfo = GraphDataConvertor.convert(agentStructInfo.getGraphData());
        for (Map.Entry<String, GraphParams> entry : graphNodeInfo.getParams().entrySet()) {
            GraphParams graphParams = entry.getValue();
            KeyType keyType = KeyType.getByKey(graphParams.paramType());
            if (keyType == null) {
                throw new IllegalArgumentException("参数类型错误");
            }
            builder.addParam(entry.getKey(), keyType, graphParams.defValue());
        }
        Map<String, ActionConfig> configs = getConfig(agentStructInfo);
        Map<String, Class<? extends KNodeAction>> nodeActionMap = new HashMap<>(graphData.nodes().size());
        for (GraphData.Node node : graphData.nodes()) {
            Class<?> nodeActionClass = ClassUtil.stringToClass(node.nodeType());
            if (KNodeAction.class.isAssignableFrom(nodeActionClass)) {
                nodeActionMap.put(node.nodeId(), (Class<? extends KNodeAction>) nodeActionClass);
            } else {
                throw new IllegalArgumentException("节点类型错误");
            }
        }
        Map<String, Class<? extends KEdgeAction>> edgeActionMap = new HashMap<>(graphData.conditionEdges().size());
        for (GraphData.Condition condition : graphData.conditionEdges()) {
            Class<?> edgeActionClass = ClassUtil.stringToClass(condition.edgeType());
            if (KEdgeAction.class.isAssignableFrom(edgeActionClass)) {
                edgeActionMap.put(condition.edgeId(), (Class<? extends KEdgeAction>) edgeActionClass);
            } else {
                throw new IllegalArgumentException("边类型错误");
            }
        }
        for (String node : graphNodeInfo.getNodes()) {
            ActionConfig actionConfig = configs.get(node);
            Class<? extends KNodeAction> nodeAction = nodeActionMap.get(actionConfig.getNodeId());
            if (nodeAction == null) {
                throw new IllegalArgumentException("节点类型缺失");
            }
            builder.addNode(node, nodeAction, actionConfig);
        }
        for (Map.Entry<String, String> entry : graphNodeInfo.getEdges().entrySet()) {
            builder.addEdge(entry.getKey(), entry.getValue());
        }
        for (GraphNodeInfo.ConditionEdge conditionEdge : graphNodeInfo.getConditionEdges().values()) {
            ActionConfig actionConfig = configs.get(conditionEdge.edgeId());
            Class<? extends KEdgeAction> edgeAction = edgeActionMap.get(conditionEdge.edgeId());
            if (edgeAction == null) {
                throw new IllegalArgumentException("边类型缺失");
            }
            builder.addConditionEdge(conditionEdge.sourceId(), conditionEdge.edgeId(), edgeAction, actionConfig, conditionEdge.mappings());
        }
        return builder.build();
    }

    public KAgent fresh(KAgent kAgent, AgentStructInfo agentStructInfo, List<PromptParams> params) {
        Map<String, PromptParams> promptParamsMap = new HashMap<>(params.size());
        for (PromptParams promptParam : params) {
            promptParamsMap.put(promptParam.promptCode(), promptParam);
        }
        for (PromptDataInfo promptDataInfo : agentStructInfo.getPrompts()) {
            PromptParams promptParams = promptParamsMap.get(promptDataInfo.getPromptCode());
            if (promptParams != null) {
                promptDataInfo.getParamsDatas().clear();
                promptDataInfo.getParamsDatas().addAll(promptParams.params());
            }
        }
        if (Objects.equals(kAgent.getAgentConfig().getVersion(), agentStructInfo.getVersion())) {
            for (ActionConfig actionConfig : kAgent.getConfigMap().values()) {
                PromptParams promptParams = promptParamsMap.get(actionConfig.getPromptCode());
                if (promptParams != null) {
                    actionConfig.getPromptInfo().paramsData().clear();
                    for (ParamData paramData : promptParams.params()) {
                        actionConfig.getPromptInfo().paramsData().put(paramData.getParamKey(), paramData.getParamData());
                    }
                }
            }
            return kAgent;
        }
        return build(agentStructInfo);
    }


    private Map<String, ActionConfig> getConfig(AgentStructInfo agentStructInfo) {
        Map<String, NodeInfo> nodeInfoMap = new HashMap<>(agentStructInfo.getConfigs().size());
        for (NodeConfigInfo nodeConfigInfo : agentStructInfo.getConfigs()) {
            nodeInfoMap.put(nodeConfigInfo.getNodeCode(), nodeConfigInfo.getNodeInfo());
        }
        Map<String, PromptInfo> prompts = ActionConfigConvertor.convert(agentStructInfo.getPrompts());
        Map<String, PromptInfo> newPrompts = new HashMap<>(prompts.size());
        for (Map.Entry<String, PromptInfo> entry : prompts.entrySet()) {
            PromptInfo promptInfo = entry.getValue();
            PromptInfo newPromptInfo = freshPrompt(promptInfo);
            newPrompts.put(entry.getKey(), newPromptInfo);
        }
        return nodeConfigService.getActionConfigMap(nodeInfoMap, newPrompts);
    }

    public @NotNull PromptInfo freshPrompt(PromptInfo promptInfo) {
        List<String> newParms = new ArrayList<>();
        String newPrompt = promptInfo.prompt();
        if (StringUtils.hasText(promptInfo.prompt())) {
            Collection<String> allCodes = modelContextManageService.allCodes();
            for (String code : allCodes) {
                if (promptInfo.prompt().contains("{" + code + "}")) {
                    newParms.add(code);
                }
            }
            newPrompt = promptInfo.prompt().replaceAll("\\{\\{([^}]*)}}", "{$1}");
        }
        PromptInfo newPromptInfo = new PromptInfo(newPrompt, newParms, promptInfo.paramsData());
        return newPromptInfo;
    }
}
