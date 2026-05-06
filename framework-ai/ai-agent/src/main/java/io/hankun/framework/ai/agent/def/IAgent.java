package io.hankun.framework.ai.agent.def;

import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.def.entity.AgentParams;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @description:
 * @className: IAgent
 * @createAt: 2025/12/25 10:01
 * @author: hankun
 */
public interface IAgent {

    String agentCode();

    String agentDesc();

    Flux<DataWithMeta> call(String sessionId, AgentParams params, TaskExecConfig taskExecConfig);

    default DataWithMeta blockCall(String sessionId, AgentParams params, TaskExecConfig taskExecConfig) {
        Flux<DataWithMeta> agentResultFlux = call(sessionId, params, taskExecConfig);
        List<DataWithMeta> block = agentResultFlux.collectList().block();
        return DataWithMeta.combine(block);
    }
}
