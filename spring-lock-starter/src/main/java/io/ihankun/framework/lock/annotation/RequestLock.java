package io.ihankun.framework.lock.annotation;

import java.lang.annotation.*;

/**
 * @author hankun
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestLock {

    /**
     * 重复提交的默认提示信息
     */
    public static final String MESSAGE = "禁止重复提交,请刷新后再试";

    /**
     * 重复提交的默认提示信息
     *
     * @return
     */
    String value() default MESSAGE;
}
