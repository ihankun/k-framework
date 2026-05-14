package io.hankun.framework.ai.context.entity;

import io.hankun.framework.ai.core.context.IContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @description:
 * @className: ContextDependent
 * @createAt: 2025/9/29 16:29
 * @author: hankun
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextDependent {
    Class<? extends IContext>[] value();
}
