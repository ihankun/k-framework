package io.ihankun.framework.common.optimistic;

import java.lang.annotation.*;

/**
 * 版本控制锁-参数注解，使用了该注解的参数，必须为自定义类的对象，且含有id、version
 * @author hankun
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockParam {
}
