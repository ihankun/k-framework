package io.hankun.framework.ai.core.record;

import io.hankun.framework.ai.core.entity.CurrentId;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description:
 * @className: TaskNodeRecord
 * @createAt: 2025/10/16 10:30
 * @author: hankun
 */
@Data
public class TaskNodeRecord {

    public record ToolCall(String toolName, String params, String result) {

    }

    private String agentId;
    private String nodeId;
    private String nodeInput;
    private String nodeOutput;
    private List<ToolCall> toolCalls;
    private Date createTime;
    private Long timeCost = -1L;


    public static TaskNodeRecord init(CurrentId currentId, String nodeInput) {
        TaskNodeRecord taskNodeRecord = new TaskNodeRecord();
        taskNodeRecord.setAgentId(currentId.agentId());
        taskNodeRecord.setNodeId(currentId.nodeId());
        taskNodeRecord.setNodeInput(nodeInput);
        taskNodeRecord.setNodeOutput("");
        taskNodeRecord.setToolCalls(new CopyOnWriteArrayList<>());
        taskNodeRecord.setCreateTime(new Date());
        return taskNodeRecord;
    }

    public void finish(String nodeOutput) {
        this.nodeOutput = nodeOutput;
        this.timeCost = System.currentTimeMillis() - this.createTime.getTime();
    }

    public void addToolCall(String toolCallName, String toolCallParams, String toolCallResult) {
        this.toolCalls.add(new ToolCall(toolCallName, toolCallParams, toolCallResult));
    }
}
