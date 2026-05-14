package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @description:
 * @className: NoBuildContextRegister
 * @createAt: 2025/11/5 10:12
 * @author: hankun
 */
public interface NoBuildContextRegister<T extends IContext> extends ContextRegister<T> {


    @Nullable
    @Override
    default T build(@NotNull CurrentId currentId, @Nullable T old, @NotNull ContextAccess contextAccess,
                    @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        return null;
    }

    @NotNull
    @Override
    default RegisterTime registerTime() {
        return RegisterTime.notAutoRegister();
    }
}
