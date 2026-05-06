package io.hankun.framework.ai.agent.entity;

import io.hankun.framework.ai.common.entity.CurrentId;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @description:
 * @className: NodeResultData
 * @createAt: 2025/12/9 09:48
 * @author: hankun
 */
public record NodeResultData(String agentId, String nodeId, String data, Map<String, Object> meta, String type) {

    public DataWithMeta toData() {
        return new DataWithMeta(data, meta, type);
    }

    public static NodeResultData of(CurrentId currentId, DataWithMeta data) {
        String agentId = "";
        String nodeId = "";
        if (currentId != null) {
            agentId = currentId.agentId();
            nodeId = currentId.nodeId();
        }
        return new NodeResultData(agentId, nodeId, data.data(), data.meta(), data.type());
    }

    public static NodeResultData of(String agentId, String nodeId, DataWithMeta data) {
        return new NodeResultData(agentId, nodeId, data.data(), data.meta(), data.type());
    }


    public static DataWithMeta combine(@Nullable Collection<NodeResultData> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return DataWithMeta.ofData("", Map.of());
        }
        StringBuilder allText = new StringBuilder();
        Map<String, Object> meta = new HashMap<>();
        for (NodeResultData data : dataList) {
            allText.append(data.data());
            meta.putAll(data.meta());
        }
        return DataWithMeta.ofData(allText.toString(), meta);
    }

    public static List<NodeResultData> merge(List<NodeResultData> dataList) {
        List<NodeResultData> result = new ArrayList<>();
        List<NodeResultData> cache = new ArrayList<>();
        String agentId = "";
        String nodeId = "";
        for (NodeResultData data : dataList) {
            if (agentId.equals(data.agentId()) && nodeId.equals(data.nodeId())) {
                cache.add(data);
            } else {
                if (!cache.isEmpty()) {
                    result.add(of(agentId, nodeId, combine(cache)));
                }
                agentId = data.agentId();
                nodeId = data.nodeId();
                cache.clear();
                cache.add(data);
            }
        }
        if (!cache.isEmpty()) {
            result.add(of(agentId, nodeId, combine(cache)));
        }
        return result;
    }
}
