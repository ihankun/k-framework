package io.hankun.framework.ai.agent.def;

import io.hankun.framework.ai.agent.def.entity.AgentParams;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;

/**
 * @description:
 * @className: TaskNode
 * @createAt: 2025/12/25 11:29
 * @author: hankun
 */
public abstract class TaskNode {


    public TaskExecStatus execute(String sessionId, ContextAccess contextAccess,
                                  AgentParams params, NodeOutput output) {
        return TaskExecStatus.COMPLETED;
    }

    public TaskExecStatus resume(String sessionId, ContextAccess contextAccess,
                                 AgentParams params, NodeOutput output) {
        return TaskExecStatus.COMPLETED;
    }
}
