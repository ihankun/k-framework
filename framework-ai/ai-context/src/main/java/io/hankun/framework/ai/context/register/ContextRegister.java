package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.session.OperateStatus;
import io.hankun.framework.ai.core.session.SessionStatus;
import io.hankun.framework.ai.core.session.node.TaskNodeStatus;
import io.hankun.framework.ai.core.session.task.TaskExecStatus;
import io.hankun.framework.ai.context.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.Ordered;

/**
 * @description:
 * @className: ContextRegister
 * @createAt: 2025/10/16 14:31
 * @author: hankun
 */
public interface ContextRegister<T extends IContext> extends Ordered, ContextTypeHolder<T> {

    @NotNull
    ContextLifecycle lifecycle();

    String desc();

    default void build(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess,
                       @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        ContextData contextData = contextAccess.contextData();
        T old = contextData.getData(getKey(), dataType());
        T newData = build(currentId, old, contextAccess, inputParams, extendParams);
        if (newData != null) {
            contextData.setData(getKey(), newData);
        } else {
            contextData.removeData(getKey());
        }
    }

    @Nullable
    T build(@NotNull CurrentId currentId, @Nullable T old, @NotNull ContextAccess contextAccess,
            @NotNull InputParams inputParams, @NotNull ContextData extendParams);

    @NotNull
    default RegisterTime registerTime() {
        RegisterTime.Builder builder = lifecycle().defRegisterTime();
        builder = registerTime(builder);
        return builder.build();
    }

    default RegisterTime.Builder registerTime(RegisterTime.Builder builder) {
        return builder;
    }


    @NotNull
    default String getKey() {
        return dataType().getCanonicalName();
    }

    default boolean isDef() {
        return true;
    }


    default T getData(ContextAccess contextAccess) {
        return contextAccess.contextData().getData(getKey(), dataType());
    }

    default void onSessionUpdate(SessionStatus sessionStatus, CurrentId currentId,
                                 ContextAccess contextAccess) {
        T data = getData(contextAccess);
        if (data != null) {
            onSessionUpdate(sessionStatus, currentId, data, contextAccess);
        }
    }

    default void onSessionUpdate(SessionStatus sessionStatus, CurrentId currentId, T data,
                                 ContextAccess contextAccess) {

    }

    default void onOperateUpdate(OperateStatus operateStatus, CurrentId currentId,
                                 ContextAccess contextAccess) {
        T data = getData(contextAccess);
        if (data != null) {
            onOperateUpdate(operateStatus, currentId, data, contextAccess);
        }
    }

    default void onOperateUpdate(OperateStatus operateStatus, CurrentId currentId, T data,
                                 ContextAccess contextAccess) {

    }

    default void onTaskUpdate(TaskExecStatus taskExecStatus, CurrentId currentId,
                              ContextAccess contextAccess) {
        T data = getData(contextAccess);
        if (data != null) {
            onTaskUpdate(taskExecStatus, currentId, data, contextAccess);
        }
    }

    default void onTaskUpdate(TaskExecStatus taskExecStatus, CurrentId currentId, T data,
                              ContextAccess contextAccess) {

    }

    default void onTaskNodeUpdate(TaskNodeStatus taskNodeStatus, CurrentId currentId,
                                  ContextAccess contextAccess) {
        T data = getData(contextAccess);
        if (data != null) {
            onTaskNodeUpdate(taskNodeStatus, currentId, data, contextAccess);
        }
    }

    default void onTaskNodeUpdate(TaskNodeStatus taskNodeStatus, CurrentId currentId, T data,
                                  ContextAccess contextAccess) {

    }
}
