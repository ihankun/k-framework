package io.hankun.framework.ai.store.history.context;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.record.TaskRecord;
import io.hankun.framework.ai.common.session.OperateStatus;
import io.hankun.framework.ai.common.session.SessionStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import io.hankun.framework.ai.context.register.RegisterTime;
import io.hankun.framework.ai.store.history.repository.TaskHistoryRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @className: TaskRecordContextRegister
 * @createAt: 2025/10/16 17:18
 * @author: hankun
 */
@Component
public class TaskRecordContextRegister implements ContextRegister<TaskRecordContext> {

    private final TaskHistoryRepository taskHistoryRepository;

    public TaskRecordContextRegister(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.MESSAGE;
    }

    @Override
    public String desc() {
        return "任务记录";
    }

    @NotNull
    @Override
    public Class<TaskRecordContext> dataType() {
        return TaskRecordContext.class;
    }

    @Override
    public TaskRecordContext build(@NotNull CurrentId currentId, TaskRecordContext old, @NotNull ContextAccess contextAccess,
                                   @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        return new TaskRecordContext(new HashMap<>());
    }

    @NotNull
    @Override
    public RegisterTime registerTime() {
        return RegisterTime.builder()
                .registerAt(SessionStatus.OPEN)
                .build();
    }

    @NotNull
    @Override
    public String getKey() {
        return "taskRecord";
    }

    @Override
    public void onOperateUpdate(OperateStatus operateStatus, CurrentId currentId, TaskRecordContext data,
                                ContextAccess contextAccess) {
        if (OperateStatus.START.equals(operateStatus)) {
            InputParams inputParams = contextAccess.getData(InputParams.class);
            TaskRecord taskRecord = TaskRecord.init(currentId, inputParams.getCallerInfo().uniqueId(),
                    inputParams.formatInput());
            data.taskRecords().put(currentId.agentId(), taskRecord);
        }
    }

    @Override
    public void onSessionUpdate(SessionStatus sessionStatus, CurrentId currentId, TaskRecordContext data,
                                ContextAccess contextAccess) {
        if (sessionStatus.isFinal()) {
            if (data != null) {
                List<TaskRecord> taskRecords = new ArrayList<>(data.taskRecords().values());
                taskRecords.sort(Comparator.comparing(TaskRecord::getCreateTime));
                taskHistoryRepository.save(taskRecords);
            }
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
