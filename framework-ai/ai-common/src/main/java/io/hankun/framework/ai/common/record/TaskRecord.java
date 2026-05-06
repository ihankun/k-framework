package io.hankun.framework.ai.common.record;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description:
 * @className: TaskRecord
 * @createAt: 2025/10/16 10:41
 * @author: hankun
 */
@Data
public class TaskRecord {
    private CurrentId currentId;
    private String userKey;
    private String input;
    private String output;
    private Map<String, Object> meta;
    private List<TaskNodeRecord> taskNodeRecords;
    private TaskExecStatus status;
    private Date createTime;
    private Long timeCost = -1L;

    public void addToolCall(CurrentId currentId, String toolName, String params, String result) {
        for (TaskNodeRecord taskNodeRecord : taskNodeRecords) {
            if (Objects.equals(taskNodeRecord.getAgentId(), currentId.agentId())) {
                if (Objects.equals(taskNodeRecord.getNodeId(), currentId.nodeId())) {
                    taskNodeRecord.addToolCall(toolName, params, result);
                }
            }
        }
    }

    public static TaskRecord init(CurrentId currentId, String userKey, String input) {
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.setCurrentId(currentId);
        taskRecord.setUserKey(userKey);
        taskRecord.setInput(input);
        taskRecord.setOutput("");
        taskRecord.setTaskNodeRecords(new CopyOnWriteArrayList<>());
        taskRecord.setCreateTime(new Date());
        return taskRecord;
    }

    public void finish(String output, Map<String, Object> meta, TaskExecStatus status) {
        this.output = output;
        this.meta = meta;
        this.status = status;
        this.timeCost = System.currentTimeMillis() - createTime.getTime();
    }
}
