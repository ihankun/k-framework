package io.hankun.framework.ai.agent.api.convertor;

import io.hankun.framework.ai.agent.api.entity.GraphData;
import io.hankun.framework.ai.agent.api.entity.GraphNodeInfo;
import io.hankun.framework.ai.agent.api.entity.GraphParams;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: GraphDataConvertor
 * @createAt: 2025/12/1 14:38
 * @author: hankun
 */
public class GraphDataConvertor {

    public static GraphNodeInfo convert(GraphData graphData) {
        GraphNodeInfo graphNodeInfo = new GraphNodeInfo();
        for (GraphParams param : graphData.params()) {
            graphNodeInfo.addParam(param.paramKey(), param.paramType(), param.defValue());
        }
        for (GraphData.Node node : graphData.nodes()) {
            graphNodeInfo.addNode(node.nodeId());
        }
        for (GraphData.Edge edge : graphData.edges()) {
            graphNodeInfo.addEdge(edge.sourceId(), edge.targetId());
        }
        for (GraphData.Condition condition : graphData.conditionEdges()) {
            Map<String, String> mappings = new HashMap<>();
            for (GraphData.ConditionMapping mapping : condition.mappings()) {
                mappings.put(mapping.mappingKey(), mapping.mappingNode());
            }
            graphNodeInfo.addConditionEdge(condition.sourceId(), condition.edgeId(), mappings);
        }
        return graphNodeInfo;
    }
}
