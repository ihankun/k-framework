package io.hankun.framework.ai.agent.task;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @className: TaskStatusData
 * @createAt: 2025/10/30 19:20
 * @author: hankun
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskStatusData {
    private String agentId;
    private volatile CurrentId currentId;
    private volatile TaskExecStatus taskExecStatus;
    private String inputRequired;
}
