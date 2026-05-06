package io.hankun.framework.ai.agent.def;

import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.def.entity.AgentParams;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: AgentCallService
 * @createAt: 2025/12/25 13:48
 * @author: hankun
 */
public class AgentCallService {

    public Flux<DataWithMeta> callAgent(String agentCode, String sessionId,
                                        AgentParams params, TaskExecConfig taskExecConfig) {
        return null;
    }

    public Flux<DataWithMeta> callAgent(String agentCode,
                                        AgentParams params) {
        return callAgent(agentCode, null, params,
                TaskExecConfig.builder().build());
    }

}
