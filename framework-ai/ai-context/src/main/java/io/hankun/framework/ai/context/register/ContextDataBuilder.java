package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @description:
 * @className: ContextDataBuilder
 * @createAt: 2025/12/22 09:18
 * @author: hankun
 */
public interface ContextDataBuilder<T> extends ContextBuilder {

    Class<T> dataType();

    default void build(@NotNull String key,
                       @NotNull CurrentId currentId,
                       @NotNull ContextAccess contextAccess,
                       @NotNull InputParams inputParams) {
        ContextData contextData = contextAccess.contextData();
        T old = contextData.getData(key, dataType());
        T newData = build(currentId, contextAccess, inputParams, old);
        if (newData != null) {
            contextData.setData(key, newData);
        } else {
            contextData.removeData(key);
        }
    }

    @Nullable
    T build(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess,
            @NotNull InputParams inputParams, @Nullable T old);

}
