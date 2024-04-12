package io.ihankun.framework.core.optimistic;

import java.lang.annotation.*;

/**
 * 版本控制锁-version字段注解，使用了该注解的字段将会被视作version
 * @author hankun
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockVersion {
}
