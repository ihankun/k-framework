package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: ContextNotBuilder
 * @createAt: 2025/12/22 09:15
 * @author: hankun
 */
public class ContextNotBuilder implements ContextBuilder {

    @NotNull
    @Override
    public RegisterTime registerTime(@NotNull ContextLifecycle lifeCycle) {
        return RegisterTime.notAutoRegister();
    }

    @Override
    public void build(@NotNull CurrentId currentId,
                      @NotNull ContextAccess contextAccess,
                      @NotNull InputParams inputParams) {

    }
}
