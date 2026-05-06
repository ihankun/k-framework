package io.hankun.framework.ai.agent.graph;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import io.hankun.framework.ai.agent.api.entity.GraphNodeInfo;
import io.hankun.framework.ai.agent.api.entity.GraphParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: GraphBuildService
 * @createAt: 2025/7/22 08:58
 * @author: hankun
 */
@Slf4j
@Component
public class GraphBuildService {

    public StateGraph buildStateGraph(GraphNodeInfo graphNodeInfo, Map<String, NodeAction> nodeActionMap,
                                      Map<String, EdgeAction> edgeActionMap) throws GraphStateException {
        StateGraph stateGraph = initChatGraph(graphNodeInfo.getParams());
        for (String nodeId : graphNodeInfo.getNodes()) {
            NodeAction node = nodeActionMap.get(nodeId);
            if (node == null) {
                throw new GraphStateException("node not found:" + nodeId);
            }
            stateGraph.addNode(nodeId, AsyncNodeAction.node_async(node));

        }
        for (Map.Entry<String, String> entry : graphNodeInfo.getEdges().entrySet()) {
            stateGraph.addEdge(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, GraphNodeInfo.ConditionEdge> entry : graphNodeInfo.getConditionEdges().entrySet()) {
            GraphNodeInfo.ConditionEdge conditionEdge = entry.getValue();
            EdgeAction edge = edgeActionMap.get(conditionEdge.edgeId());
            if (edge == null) {
                throw new GraphStateException("edge not found:" + conditionEdge.edgeId());
            }
            stateGraph.addConditionalEdges(conditionEdge.sourceId(),
                    AsyncEdgeAction.edge_async(edge),
                    conditionEdge.mappings());
        }
        return stateGraph;
    }

    public CompiledGraph buildGraph(GraphNodeInfo graphNodeInfo, Map<String, NodeAction> nodeActionMap,
                                    Map<String, EdgeAction> edgeActionMap) {
        try {
            return buildStateGraph(graphNodeInfo, nodeActionMap, edgeActionMap).compile();
        } catch (GraphStateException e) {
            log.error("Graph state error: e=", e);
            throw new RuntimeException(e);
        }
    }

    private StateGraph initChatGraph(Map<String, GraphParams> params) {
        KeyStrategyFactory overAllStateFactory = () -> {
            Map<String, KeyStrategy> keys = new HashMap<>(params.size());
            for (Map.Entry<String, GraphParams> entry : params.entrySet()) {
                keys.put(entry.getKey(), buildStrategy(entry.getValue().paramType()));
            }
            return keys;
        };
        return new StateGraph(overAllStateFactory);
    }

    private KeyStrategy buildStrategy(String key) {
        KeyType strategyType = KeyType.getByKey(key);
        if (strategyType != null) {
            return strategyType.buildStrategy();
        }
        throw new RuntimeException("未定义的keyStrategy:" + key);
    }
}
