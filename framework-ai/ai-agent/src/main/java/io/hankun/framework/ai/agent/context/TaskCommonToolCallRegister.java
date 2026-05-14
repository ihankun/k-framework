package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @description:
 * @className: TaskCommonToolCallRegister
 * @createAt: 2025/10/28 14:55
 * @author: hankun
 */
@Component
public class TaskCommonToolCallRegister implements ContextRegister<TaskCommonToolCallContext> {

    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.TASK_IN_AGENT;
    }

    @Override
    public String desc() {
        return "工具调用记录";
    }

    @NotNull
    @Override
    public Class<TaskCommonToolCallContext> dataType() {
        return TaskCommonToolCallContext.class;
    }

    @Override
    public TaskCommonToolCallContext build(@NotNull CurrentId currentId, TaskCommonToolCallContext old, @NotNull ContextAccess contextAccess,
                                           @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        if (old == null) {
            return new TaskCommonToolCallContext(new ArrayList<>());
        }
        return old;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
