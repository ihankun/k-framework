package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @description:
 * @className: MemContextRegister
 * @createAt: 2025/12/4 17:05
 * @author: hankun
 */
@Component
public class MemContextRegister implements ContextRegister<MemContext> {
    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.TASK;
    }

    @Override
    public String desc() {
        return "记忆";
    }

    @Nullable
    @Override
    public MemContext build(@NotNull CurrentId currentId, @Nullable MemContext old,
                            @NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        return new MemContext(new HashMap<>());
    }

    @NotNull
    @Override
    public Class<MemContext> dataType() {
        return MemContext.class;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
