package io.hankun.framework.ai.context.events;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.session.OperateStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: OperateEventListener
 * @createAt: 2025/11/3 11:05
 * @author: hankun
 */
public interface OperateEventListener extends ContextEventListener {

    void onOperateUpdate(@NotNull OperateStatus operateStatus, @NotNull CurrentId currentId,
                         @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams);

}
