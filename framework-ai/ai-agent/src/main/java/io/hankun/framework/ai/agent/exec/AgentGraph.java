package io.hankun.framework.ai.agent.exec;

import io.hankun.framework.ai.agent.KAgent;
import io.hankun.framework.ai.agent.config.AgentConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: AgentGraph
 * @createAt: 2025/10/31 13:51
 * @author: hankun
 */
public record AgentGraph(Map<String, KAgent> agentMap, Map<String, String> nextMap) {

    public KAgent getNext(String agentCode) {
        String next = nextMap.get(agentCode);
        if (next == null) {
            return null;
        }
        return agentMap.get(next);
    }

    public KAgent getAgent(String agentCode) {
        return agentMap.get(agentCode);
    }

    public AgentConfig getConfig(String agentCode) {
        KAgent kAgent = agentMap.get(agentCode);
        if (kAgent == null) {
            return null;
        }
        return kAgent.getAgentConfig();
    }

    public static AgentGraph build(List<KAgent> kAgents) {
        Map<String, KAgent> agentMap = new HashMap<>();
        Map<String, String> nextMap = new HashMap<>();
        String code = "__start__";
        for (KAgent kAgent : kAgents) {
            agentMap.put(kAgent.getCode(), kAgent);
            nextMap.put(code, kAgent.getCode());
            code = kAgent.getCode();
        }
        nextMap.put(code, "__end__");
        return new AgentGraph(agentMap, nextMap);
    }
}
