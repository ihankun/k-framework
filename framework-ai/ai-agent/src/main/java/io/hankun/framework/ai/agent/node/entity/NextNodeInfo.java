package io.hankun.framework.ai.agent.node.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: NextNodeInfo
 * @createAt: 2025/10/28 11:44
 * @author: hankun
 */
@Data
public class NextNodeInfo {
    private String nextNode;
    private Map<String, String> targetNodes = new HashMap<>();

    public String fetchNextNode(String key) {
        return targetNodes.get(key);
    }
}
