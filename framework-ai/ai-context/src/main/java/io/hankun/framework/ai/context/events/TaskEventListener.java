package io.hankun.framework.ai.context.events;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: TaskEventListener
 * @createAt: 2025/11/3 11:06
 * @author: hankun
 */
public interface TaskEventListener extends ContextEventListener {

    void onTaskUpdate(@NotNull TaskExecStatus taskExecStatus,
                      @NotNull CurrentId currentId,
                      @NotNull ContextAccess contextAccess,
                      @NotNull InputParams inputParams);

}
