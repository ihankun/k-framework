package io.hankun.framework.ai.agent.task;

import io.hankun.framework.ai.common.session.SessionStatus;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import lombok.Data;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @className: SessionStatusData
 * @createAt: 2025/10/30 19:22
 * @author: hankun
 */
@Data
public class SessionStatusData {
    private String taskId;

    private volatile SessionStatus status;

    private ConcurrentHashMap<String, TaskStatusData> runningTasks = new ConcurrentHashMap<>();

    public boolean checkFinish(String agentId) {
        if (runningTasks.isEmpty()) {
            return true;
        }
        TaskStatusData taskStatusData = runningTasks.get(agentId);
        if (taskStatusData == null) {
            return true;
        }
        return taskStatusData.getTaskExecStatus().isFinal();
    }

    public TaskStatusData getTask(String agentId) {
        return runningTasks.get(agentId);
    }

    public TaskStatusData loadTask(String agentId) {
        return runningTasks.computeIfAbsent(agentId, id -> {
            return new TaskStatusData(agentId, null, TaskExecStatus.INIT, "");
        });
    }

    public Collection<TaskStatusData> showTasks() {
        return runningTasks.values();
    }

    public boolean checkInterrupt() {
        for (TaskStatusData taskStatusData : runningTasks.values()) {
            if (TaskExecStatus.INPUT_REQUIRED.equals(taskStatusData.getTaskExecStatus())) {
                return true;
            }
        }
        return false;
    }

    public void remove(String agentId) {
        runningTasks.remove(agentId);
    }
}
