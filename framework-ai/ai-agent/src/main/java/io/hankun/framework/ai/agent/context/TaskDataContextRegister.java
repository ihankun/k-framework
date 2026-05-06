package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description:
 * @className: TaskDataContextRegister
 * @createAt: 2025/10/20 15:22
 * @author: hankun
 */
@Component
public class TaskDataContextRegister implements ContextRegister<TaskDataContext> {
    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.TASK_IN_AGENT;
    }

    @Override
    public String desc() {
        return "任务数据";
    }

    @NotNull
    @Override
    public Class<TaskDataContext> dataType() {
        return TaskDataContext.class;
    }

    @Override
    public TaskDataContext build(@NotNull CurrentId currentId, TaskDataContext old, @NotNull ContextAccess contextAccess,
                                 @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        return Objects.requireNonNullElseGet(old, TaskDataContext::build);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void onTaskUpdate(TaskExecStatus taskExecStatus, CurrentId currentId,
                             TaskDataContext data, ContextAccess contextAccess) {
        if (data != null) {
            if (TaskExecStatus.WORKING.equals(taskExecStatus)) {
                data.resetInterrupt();
            }
        }
    }
}
