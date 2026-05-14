package io.hankun.framework.ai.context.model;

import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextTypeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @description:
 * @className: UserContextDataBuilder
 * @createAt: 2025/11/3 10:49
 * @author: hankun
 */
public interface UserContextDataBuilder<T extends IContext> extends UserContextBuilder, ContextTypeHolder<T> {


    @Nullable
    @Override
    default UserContextInfo buildUserContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess) {
        T data = contextAccess.getDataOrNull(dataType());
        return buildUserContextInfo(currentId, contextAccess, data);
    }

    @Nullable
    UserContextInfo buildUserContextInfo(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, @Nullable T data);
}
