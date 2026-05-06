package io.hankun.framework.ai.context.entity;

import io.hankun.framework.ai.common.context.IContext;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: ContextTypeHolder
 * @createAt: 2025/11/3 10:52
 * @author: hankun
 */
public interface ContextTypeHolder<T extends IContext> {

    /**
     * 上下文类型
     *
     * @return 上下文类型
     */
    @NotNull
    Class<T> dataType();
}
