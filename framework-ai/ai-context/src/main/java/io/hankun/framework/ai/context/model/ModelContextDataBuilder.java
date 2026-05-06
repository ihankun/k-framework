package io.hankun.framework.ai.context.model;

import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextTypeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @description:
 * @className: ModelContextDataBuilder
 * @createAt: 2025/10/17 16:55
 * @author: hankun
 */
public interface ModelContextDataBuilder<T extends IContext> extends ModelContextBuilder, ContextTypeHolder<T> {

    @Nullable
    @Override
    default Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess) {
        T data = contextAccess.getDataOrNull(dataType());
        return buildContext(currentId, contextAccess, data);
    }

    @Nullable
    Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, @Nullable T data);
}
