package io.hankun.framework.ai.context.model;

import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: ContextBuilder
 * @createAt: 2025/11/3 10:50
 * @author: hankun
 */
public interface ContextBuilder {

    /**
     * 上下文code
     *
     * @return 上下文code
     */
    @NotNull
    String code();

    /**
     * 上下文描述
     *
     * @return 上下文描述
     */
    String desc();
}
