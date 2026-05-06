package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.call.entity.AgentCallParams;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.task.TaskStatusData;

/**
 * @description:
 * @className: FluxTaskCallCallback
 * @createAt: 2025/12/11 20:05
 * @author: hankun
 */
public interface FluxTaskCallCallback {

    void beforeCall(AgentCallParams params, GlobalOutput globalOutput);

    void afterCall(AgentCallParams params, AgentCallResponse agentCallResponse, TaskStatusData taskStatusData,
                   GlobalOutput globalOutput);
}
