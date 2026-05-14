package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: ContextBuilder
 * @createAt: 2025/12/22 09:12
 * @author: hankun
 */
public interface ContextBuilder {

    @NotNull
    default RegisterTime registerTime(@NotNull ContextLifecycle lifeCycle) {
        RegisterTime.Builder builder = lifeCycle.defRegisterTime();
        builder = registerTime(builder);
        return builder.build();
    }

    @NotNull
    default RegisterTime.Builder registerTime(@NotNull RegisterTime.Builder builder) {
        return builder;
    }

    void build(@NotNull CurrentId currentId,
               @NotNull ContextAccess contextAccess,
               @NotNull InputParams inputParams);


}
