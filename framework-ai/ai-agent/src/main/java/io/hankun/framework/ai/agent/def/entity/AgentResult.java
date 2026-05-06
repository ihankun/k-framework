package io.hankun.framework.ai.agent.def.entity;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: AgentResult
 * @createAt: 2025/12/25 10:55
 * @author: hankun
 */
public record AgentResult(String data,
                          String thinking,
                          Map<String, Object> meta,
                          String type) {
    public static AgentResult combine(List<AgentResult> block) {
        if (CollectionUtils.isEmpty(block)) {
            return new AgentResult("", "", Map.of(), "");
        }
        StringBuilder allText = new StringBuilder();
        StringBuilder allThinking = new StringBuilder();
        Map<String, Object> meta = new HashMap<>();
        return null;
    }
}
