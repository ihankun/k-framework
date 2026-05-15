package io.hankun.framework.cache.requestlock;

import java.lang.annotation.*;

/**
 * 防重复提交注解
 * 作用于方法级别，防止短时间内重复提交请求
 *
 * @author hankun
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestLock {

    /**
     * 重复提交的默认提示信息
     */
    String MESSAGE = "禁止重复提交,请刷新后再试";

    /**
     * 重复提交的提示信息
     *
     * @return 提示信息
     */
    String value() default MESSAGE;
}
