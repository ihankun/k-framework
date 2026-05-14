package io.hankun.framework.ai.context.events;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.session.SessionStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: SessionEventListener
 * @createAt: 2025/11/3 11:04
 * @author: hankun
 */
public interface SessionEventListener extends ContextEventListener {

    void onSessionUpdate(@NotNull SessionStatus sessionStatus, @NotNull CurrentId currentId,
                         @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams);
}
