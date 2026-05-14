package io.hankun.framework.ai.tools.contexts;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @description:
 * @className: TimeContextRegister
 * @createAt: 2025/10/20 11:44
 * @author: hankun
 */
@Component
public class TimeContextRegister implements ContextRegister<TimeContext> {
    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.TASK_IN_AGENT;
    }

    @Override
    public String desc() {
        return "当前时间";
    }

    @NotNull
    @Override
    public Class<TimeContext> dataType() {
        return TimeContext.class;
    }

    @Override
    public TimeContext build(@NotNull CurrentId currentId, TimeContext old, @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        return new TimeContext(LocalDateTime.now());
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
