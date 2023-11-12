package io.ihankun.framework.common.optimistic;

import java.lang.annotation.*;

/**
 * 标识乐观锁id的注解
 * @author hankun
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockId {

    /**
     * id的默认值
     */
    String value();
}
