package io.ihankun.framework.common.v1.optimistic;

import java.lang.annotation.*;

/**
 * 版本控制锁-方法注解，value为所要进行更改数据的表名，使用该注解的方法，将会被切面拦截处理
 * @author hankun
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

    /**
     * 乐观锁表名
     */
    String value();

    /**
     * 校验失败提示信息
     */
    String message() default "数据已变更，请刷新重试";
}
