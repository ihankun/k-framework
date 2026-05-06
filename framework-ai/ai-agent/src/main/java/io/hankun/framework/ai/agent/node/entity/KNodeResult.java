package io.hankun.framework.ai.agent.node.entity;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.node.NodeUtil;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfigHolder;
import io.hankun.framework.commons.utils.ContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KNodeResult
 * @createAt: 2025/10/27 14:20
 * @author: hankun
 */
public record KNodeResult(String nodeId, Map<String, Object> dataMap) {


    public String buildNodeOutputJson() {
        Map<String, Object> result = new HashMap<>(dataMap);
        result.remove(NodeUtil.CURRENT_ID);
        return JSON.toJSONString(result);
    }

    public static KNodeResult of(String nodeId, Map<String, Object> dataMap) {
        if (dataMap == null) {
            throw new IllegalArgumentException("params is null");
        }
        Map<String, Object> result = new HashMap<>(dataMap);
        if (dataMap.containsKey(NodeUtil.RESULT)) {
            return new KNodeResult(nodeId, result);
        }
        result.put(NodeUtil.RESULT, DataWithMetaContext.of());
        return new KNodeResult(nodeId, result);
    }

    public static KNodeResult of(Map<String, Object> result) {
        String nodeId = getNodeId();
        return of(nodeId, result);
    }

    private static String getNodeId() {
        ActionConfig actionConfig = ActionConfigHolder.get();
        if (actionConfig == null) {
            throw new IllegalArgumentException("actionConfig is null");
        }
        return actionConfig.getNodeId();
    }

    public static KNodeResult ofResult(String nodeId, DataWithMeta result) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(NodeUtil.RESULT, result);
        return of(nodeId, resultMap);
    }

    public static KNodeResult ofResult(DataWithMeta result) {
        return ofResult(getNodeId(), result);
    }

    public static KNodeResult ofResult(String nodeId, List<DataWithMeta> result) {
        return ofResult(nodeId, DataWithMeta.combine(result));
    }

    public static KNodeResult ofResult(List<DataWithMeta> result) {
        return ofResult(getNodeId(), result);
    }

    public DataWithMeta getResult() {
        return ContextUtil.getData(dataMap, NodeUtil.RESULT);
    }

    public static <T> KNodeResult ofEntity(String nodeId, T entity) {
        DataWithMeta dataWithMeta = DataWithMeta.ofEntity(entity);
        return ofResult(nodeId, dataWithMeta);
    }

    public static <T> KNodeResult ofEntity(T entity) {
        return ofEntity(getNodeId(), entity);
    }

    public static KNodeResult ofText(String nodeId, String result) {
        return ofResult(nodeId, DataWithMeta.ofText(result));
    }

    public static KNodeResult ofText(String result) {
        return ofText(getNodeId(), result);
    }

    public static KNodeResult ofEmptyResult() {
        return ofResult(getNodeId(), List.of());
    }
}
