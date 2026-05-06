package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @className: GraphData
 * @createAt: 2025/12/1 14:34
 * @author: hankun
 */
public record GraphData(
        @Schema(description = "在节点前中断")
        List<String> interruptsBefore,
        @Schema(description = "在节点后中断")
        List<String> interruptsAfter,
        @Schema(description = "是否在edge前中断")
        Boolean interruptBeforeEdge,
        List<GraphParams> params, List<Node> nodes,
        List<Edge> edges, List<Condition> conditionEdges) {

    public record Node(String nodeId, String nodeType) {

    }

    public record Edge(String sourceId, String targetId) {
    }

    public record Condition(String sourceId, String edgeId, String edgeType, List<ConditionMapping> mappings) {

    }

    public record ConditionMapping(String mappingKey, String mappingNode) {
    }


    public static GraphData of(Collection<String> interruptsBefore, Collection<String> interruptsAfter, Boolean interruptBeforeEdge) {
        return new GraphData(new ArrayList<>(interruptsBefore), new ArrayList<>(interruptsAfter), interruptBeforeEdge,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
