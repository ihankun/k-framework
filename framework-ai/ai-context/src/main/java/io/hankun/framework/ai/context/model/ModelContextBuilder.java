package io.hankun.framework.ai.context.model;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @description:
 * @className: ModelContextBuilder
 * @createAt: 2025/10/17 14:43
 * @author: hankun
 */
public interface ModelContextBuilder extends ContextBuilder {

    @Nullable
    Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess);
}
