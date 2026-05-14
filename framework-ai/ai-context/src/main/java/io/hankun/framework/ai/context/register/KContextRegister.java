package io.hankun.framework.ai.context.register;

import io.hankun.framework.ai.context.entity.ContextLifecycle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @description:
 * @className: KContextRegister
 * @createAt: 2025/12/22 09:08
 * @author: hankun
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface KContextRegister {

    ContextLifecycle lifecycle() default ContextLifecycle.MESSAGE_IN_AGENT;

    String desc() default "";

    boolean isDefault() default true;

    String key() default "";

    Class<? extends ContextBuilder> builder() default ContextNotBuilder.class;
}
