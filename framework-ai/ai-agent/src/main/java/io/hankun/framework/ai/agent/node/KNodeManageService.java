package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.config.AgentConfig;
import io.hankun.framework.ai.agent.entity.NodeDesc;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.task.StatusManageService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: MsunNodeManageService
 * @createAt: 2025/10/21 19:35
 * @author: hankun
 */
@Component
public class KNodeManageService {

    private final Map<Class<?>, KNodeAction> nodeActionMap;

    private final Map<Class<?>, KEdgeAction> edgeActionMap;

    private final StatusManageService statusManageService;

    public KNodeManageService(List<KNodeAction> nodeActions,
                              List<KEdgeAction> edgeActions,
                              StatusManageService statusManageService) {
        this.nodeActionMap = new HashMap<>(nodeActions.size());
        this.statusManageService = statusManageService;
        for (KNodeAction nodeAction : nodeActions) {
            this.nodeActionMap.put(nodeAction.getId(), nodeAction);
        }
        this.edgeActionMap = new HashMap<>(nodeActions.size());
        for (KEdgeAction edgeAction : edgeActions) {
            this.edgeActionMap.put(edgeAction.getId(), edgeAction);
        }
    }

    public KNode buildNode(ActionConfig actionConfig, AgentConfig agentConfig, KAgent kAgent, Class<? extends KNodeAction> nodeType) {
        KNodeAction nodeAction = nodeActionMap.get(nodeType);
        if (nodeAction == null) {
            throw new IllegalArgumentException("node not found:" + nodeType);
        }
        actionConfig.withAiNode(nodeAction.aiNode());
        actionConfig.withNodeDesc(nodeAction.desc());
        return new KNode(ActionConfig.merge(nodeAction.defConfig(), actionConfig), agentConfig,
                statusManageService, kAgent, nodeAction);
    }

    public KEdgeNode buildEdge(ActionConfig actionConfig, AgentConfig agentConfig, KAgent kAgent, Class<? extends KEdgeAction> edgeType) {
        KEdgeAction edgeAction = edgeActionMap.get(edgeType);
        if (edgeAction == null) {
            throw new IllegalArgumentException("edge not found:" + edgeType);
        }
        actionConfig.withAiNode(edgeAction.aiNode());
        actionConfig.withNodeDesc(edgeAction.desc());
        return new KEdgeNode(ActionConfig.merge(edgeAction.defConfig(), actionConfig), agentConfig,
                statusManageService, kAgent, edgeAction);
    }

    public List<NodeDesc> listNode() {
        List<NodeDesc> nodeDescList = new ArrayList<>(nodeActionMap.size());
        for (KNodeAction nodeAction : nodeActionMap.values()) {
            nodeDescList.add(NodeDesc.node(nodeAction));
        }
        return nodeDescList;
    }

    public List<NodeDesc> listEdge() {
        List<NodeDesc> nodeDescList = new ArrayList<>(edgeActionMap.size());
        for (KEdgeAction edgeAction : edgeActionMap.values()) {
            nodeDescList.add(NodeDesc.edge(edgeAction));
        }
        return nodeDescList;
    }
}
