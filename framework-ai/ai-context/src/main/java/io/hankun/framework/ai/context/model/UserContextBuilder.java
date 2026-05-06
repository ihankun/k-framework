package io.hankun.framework.ai.context.model;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @description:
 * @className: UserContextBuilder
 * @createAt: 2025/11/3 10:49
 * @author: hankun
 */
public interface UserContextBuilder extends ContextBuilder {

    @Nullable
    UserContextInfo buildUserContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess);
}
