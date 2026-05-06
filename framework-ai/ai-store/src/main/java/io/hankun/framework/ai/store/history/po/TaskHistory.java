package io.hankun.framework.ai.store.history.po;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.record.TaskNodeRecord;
import io.hankun.framework.ai.common.record.TaskRecord;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: TaskHistory
 * @createAt: 2025/10/16 17:01
 * @author: hankun
 */
@Document(collection = "taskHistory")
@Data
public class TaskHistory {
    @Id
    private ObjectId id;
    @Indexed
    private String taskId;
    @Indexed
    private String userKey;
    @Indexed
    private String sessionId;
    private String agentId;
    private String input;
    private String output;
    private Map<String, Object> meta;
    private List<TaskNodeRecord> taskNodeRecords;
    private TaskExecStatus status;
    private Date createTime;
    private Long timeCost = -1L;

    public static TaskHistory of(TaskRecord taskRecord) {
        TaskHistory taskHistory = new TaskHistory();
        CurrentId currentId = taskRecord.getCurrentId();
        taskHistory.setId(new ObjectId());
        taskHistory.setTaskId(currentId.taskId());
        taskHistory.setUserKey(taskRecord.getUserKey());
        taskHistory.setSessionId(currentId.sessionId());
        taskHistory.setAgentId(currentId.agentId());
        taskHistory.setInput(taskRecord.getInput());
        taskHistory.setOutput(taskRecord.getOutput());
        taskHistory.setMeta(taskRecord.getMeta());
        taskHistory.setTaskNodeRecords(taskRecord.getTaskNodeRecords());
        taskHistory.setStatus(taskRecord.getStatus());
        taskHistory.setCreateTime(taskRecord.getCreateTime());
        taskHistory.setTimeCost(taskRecord.getTimeCost());
        return taskHistory;
    }
}
