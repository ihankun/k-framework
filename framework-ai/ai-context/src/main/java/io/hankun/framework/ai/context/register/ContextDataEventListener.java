package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.session.OperateStatus;
import io.hankun.framework.ai.common.session.SessionStatus;
import io.hankun.framework.ai.common.session.node.TaskNodeStatus;
import io.hankun.framework.ai.common.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.events.OperateEventListener;
import io.hankun.framework.ai.context.events.SessionEventListener;
import io.hankun.framework.ai.context.events.TaskEventListener;
import io.hankun.framework.ai.context.events.TaskNodeEventListener;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: ContextDataEventListener
 * @createAt: 2025/12/22 09:23
 * @author: hankun
 */
public interface ContextDataEventListener<T> extends OperateEventListener, SessionEventListener,
        TaskEventListener, TaskNodeEventListener {

    T getData(ContextAccess contextAccess);

    default void onSessionUpdate(@NotNull SessionStatus sessionStatus, @NotNull CurrentId currentId,
                                 @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        T data = getData(contextAccess);
        if (data != null) {
            onSessionUpdate(sessionStatus, currentId, data, contextAccess, inputParams);
        }
    }

    default void onSessionUpdate(SessionStatus sessionStatus, CurrentId currentId, T data,
                                 ContextAccess contextAccess, InputParams inputParams) {

    }

    default void onOperateUpdate(@NotNull OperateStatus operateStatus, @NotNull CurrentId currentId,
                                 @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        T data = getData(contextAccess);
        if (data != null) {
            onOperateUpdate(operateStatus, currentId, data, contextAccess, inputParams);
        }
    }

    default void onOperateUpdate(OperateStatus operateStatus, CurrentId currentId, T data,
                                 ContextAccess contextAccess, InputParams inputParams) {

    }

    default void onTaskUpdate(@NotNull TaskExecStatus taskExecStatus, @NotNull CurrentId currentId,
                              @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        T data = getData(contextAccess);
        if (data != null) {
            onTaskUpdate(taskExecStatus, currentId, data, contextAccess, inputParams);
        }
    }

    default void onTaskUpdate(TaskExecStatus taskExecStatus, CurrentId currentId, T data,
                              ContextAccess contextAccess, InputParams inputParams) {

    }

    default void onTaskNodeUpdate(@NotNull TaskNodeStatus taskNodeStatus, @NotNull CurrentId currentId,
                                  @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams) {
        T data = getData(contextAccess);
        if (data != null) {
            onTaskNodeUpdate(taskNodeStatus, currentId, data, contextAccess, inputParams);
        }
    }

    default void onTaskNodeUpdate(TaskNodeStatus taskNodeStatus, CurrentId currentId, T data,
                                  ContextAccess contextAccess, InputParams inputParams) {

    }
}
