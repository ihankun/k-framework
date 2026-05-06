package io.hankun.framework.ai.agent.api.entity;

import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @description:
 * @className: GraphNodeInfo
 * @createAt: 2025/7/21 17:57
 * @author: hankun
 */
@Getter
public class GraphNodeInfo {

    public static final String END = "__END__";
    public static final String START = "__START__";

    private final Map<String, GraphParams> params = new HashMap<>();

    private final Set<String> nodes = new HashSet<>();

    private final Map<String, String> edges = new HashMap<>();

    private final Map<String, ConditionEdge> conditionEdges = new HashMap<>();

    public record ConditionEdge(String sourceId, String edgeId, Map<String, String> mappings) {
    }

    public GraphNodeInfo addParam(String nodeId, String type) {
        return addParam(nodeId, type, null);
    }

    public GraphNodeInfo addParam(String nodeId, String type, Object def) {
        params.put(nodeId, new GraphParams(nodeId, type, def));
        return this;
    }

    public GraphNodeInfo addNode(String nodeId) {
        nodes.add(nodeId);
        return this;
    }

    public GraphNodeInfo addEdge(String sourceId, String targetId) {
        edges.put(sourceId, targetId);
        return this;
    }

    public GraphNodeInfo startEdge(String nodeId) {
        return addEdge(START, nodeId);
    }

    public GraphNodeInfo endEdge(String nodeId) {
        return addEdge(nodeId, END);
    }

    public GraphNodeInfo addConditionEdge(String sourceId, String edgeId, Map<String, String> mappings) {
        ConditionEdge conditionEdge = new ConditionEdge(sourceId, edgeId, new HashMap<>(mappings));
        conditionEdges.put(sourceId, conditionEdge);
        return this;
    }

    public GraphNodeInfo addCondition(String edgeId, String edgeResult, String mappingNode) {
        for (Map.Entry<String, ConditionEdge> entry : conditionEdges.entrySet()) {
            if (entry.getValue().edgeId().equals(edgeId)) {
                entry.getValue().mappings().put(edgeResult, mappingNode);
            }
        }
        return this;
    }

    /**
     * 替换节点，将所有指向oldNodeId的指向，指向newNodeId
     * oldNodeId需为为普通节点
     *
     * @param oldNodeId 源节点
     * @param newNodeId 新节点
     */
    private void replaceFrom(String oldNodeId, String newNodeId) {
        for (Map.Entry<String, String> entry : edges.entrySet()) {
            if (entry.getValue().equals(oldNodeId)) {
                entry.setValue(newNodeId);
            }
        }
        for (Map.Entry<String, ConditionEdge> entry : conditionEdges.entrySet()) {
            //替换conditionEdge的下游节点
            for (Map.Entry<String, String> mapping : entry.getValue().mappings().entrySet()) {
                if (mapping.getValue().equals(oldNodeId)) {
                    entry.getValue().mappings().put(mapping.getKey(), newNodeId);
                }
            }
        }
    }

    /**
     * 替换节点，将oldNodeId指向的节点，指向newNodeId
     * oldNodeId需为为普通节点，oldNodeId的后继需要为普通节点
     *
     * @param oldNodeId 源节点
     * @param newNodeId 新节点
     */
    private void replaceTo(String oldNodeId, String newNodeId) {
        String targetNode = edges.get(oldNodeId);
        if (targetNode != null) {
            edges.remove(oldNodeId);
            edges.put(newNodeId, targetNode);
        }
    }

    public void replaceNode(String oldNodeId, String newNodeId) {
        if (ObjectUtils.isEmpty(oldNodeId) || ObjectUtils.isEmpty(newNodeId)) {
            return;
        }
        if (Objects.equals(oldNodeId, newNodeId)) {
            return;
        }
        //如果是普通节点，则替换
        if (nodes.contains(oldNodeId)) {
            nodes.remove(oldNodeId);
            nodes.add(newNodeId);
            replaceFrom(oldNodeId, newNodeId);
            replaceTo(oldNodeId, newNodeId);
        }
        // oldNodeId的后继是conditionEdge，进行替换
        ConditionEdge conditionEdge = conditionEdges.get(oldNodeId);
        if (conditionEdge != null) {
            conditionEdges.put(newNodeId, new ConditionEdge(newNodeId,
                    conditionEdge.edgeId(), conditionEdge.mappings()));
            conditionEdges.remove(oldNodeId);
        }
        for (Map.Entry<String, ConditionEdge> entry : conditionEdges.entrySet()) {
            //oldNodeId为conditionEdge
            if (entry.getValue().edgeId().equals(oldNodeId)) {
                entry.setValue(new ConditionEdge(entry.getValue().sourceId(),
                        newNodeId, entry.getValue().mappings()));
            }
        }
    }

    public List<String> fetchNextNode(String nodeId) {
        String nextNode = edges.get(nodeId);
        if (nextNode != null) {
            return List.of(nextNode);
        }
        ConditionEdge conditionEdge = conditionEdges.get(nodeId);
        if (conditionEdge != null) {
            return List.of(conditionEdge.edgeId());
        }
        for (Map.Entry<String, ConditionEdge> entry : conditionEdges.entrySet()) {
            if (entry.getValue().edgeId().equals(nodeId)) {
                return new ArrayList<>(entry.getValue().mappings().values());
            }
        }
        throw new IllegalStateException("No next node found");
    }

    public List<String> fetchBeforeNode(String nodeId) {
        List<String> beforeNodes = new ArrayList<>();
        for (Map.Entry<String, String> entry : edges.entrySet()) {
            if (entry.getValue().equals(nodeId)) {
                if (!beforeNodes.contains(entry.getKey())) {
                    beforeNodes.add(entry.getKey());
                }
            }
        }
        for (Map.Entry<String, ConditionEdge> entry : conditionEdges.entrySet()) {
            if (entry.getValue().edgeId().equals(nodeId)) {
                if (!beforeNodes.contains(entry.getKey())) {
                    beforeNodes.add(entry.getKey());
                }
            }
            for (Map.Entry<String, String> mapping : entry.getValue().mappings().entrySet()) {
                if (mapping.getValue().equals(nodeId)) {
                    if (!beforeNodes.contains(entry.getValue().edgeId())) {
                        beforeNodes.add(entry.getValue().edgeId());
                    }
                }
            }
        }
        return beforeNodes;
    }

    public String fetchStartNode() {
        for (Map.Entry<String, String> entry : edges.entrySet()) {
            if (entry.getKey().equals(START)) {
                return entry.getValue();
            }
        }
        throw new IllegalStateException("No start node found");
    }

    public List<String> fetchEndNode() {
        List<String> endNodes = new ArrayList<>();
        for (Map.Entry<String, String> entry : edges.entrySet()) {
            if (entry.getValue().equals(END)) {
                endNodes.add(entry.getKey());
            }
        }
        for (Map.Entry<String, ConditionEdge> entry : conditionEdges.entrySet()) {
            for (Map.Entry<String, String> mapping : entry.getValue().mappings().entrySet()) {
                if (mapping.getValue().equals(END)) {
                    endNodes.add(entry.getKey());
                }
            }
        }
        return endNodes;
    }


    public static GraphNodeInfo merge(GraphNodeInfo source, GraphNodeInfo target, String replaceNodeId) {

        String startNodeId = target.fetchStartNode();

        GraphNodeInfo result = new GraphNodeInfo();
        List<String> endNodeIds = target.fetchEndNode();
        String next = source.getEdges().get(replaceNodeId);
        result.nodes.addAll(source.nodes);
        result.nodes.remove(replaceNodeId);
        result.nodes.addAll(target.nodes);

        result.edges.putAll(target.edges);
        result.getEdges().remove(START);

        result.edges.putAll(source.edges);

        result.conditionEdges.putAll(source.conditionEdges);
        result.conditionEdges.putAll(target.conditionEdges);
        result.params.putAll(source.params);
        result.params.putAll(target.params);

        result.replaceFrom(replaceNodeId, startNodeId);
        if (ObjectUtils.isEmpty(next)) {
            throw new IllegalStateException("No normal next node found");
        }
        for (String endNodeId : endNodeIds) {
            result.edges.put(endNodeId, next);
        }
        return result;
    }

}
