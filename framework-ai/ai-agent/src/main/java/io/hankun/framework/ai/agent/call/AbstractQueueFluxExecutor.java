package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.call.entity.AgentCallParams;

/**
 * @description:
 * @className: AbstractQueueFluxExecutor
 * @createAt: 2025/12/23 11:39
 * @author: hankun
 */
public abstract class AbstractQueueFluxExecutor implements ICallExecutor {

    protected QueueAgentExecutor queueAgentExecutor;


    @Override
    public void input(AgentCallParams params) {
        queueAgentExecutor.addParams(params);
    }

    @Override
    public void finish() {
        queueAgentExecutor.finish();
    }
}
