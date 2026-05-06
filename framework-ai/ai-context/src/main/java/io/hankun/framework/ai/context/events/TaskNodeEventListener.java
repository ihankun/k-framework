package io.hankun.framework.ai.context.events;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.session.node.TaskNodeStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: TaskNodeEventListener
 * @createAt: 2025/11/3 11:06
 * @author: hankun
 */
public interface TaskNodeEventListener extends ContextEventListener {

    void onTaskNodeUpdate(@NotNull TaskNodeStatus taskNodeStatus, @NotNull CurrentId currentId,
                          @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams);
}
