package io.hankun.framework.springcloud.server.auth;

import java.lang.annotation.*;

/**
 * @author hankun
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthPoint {

    Product[] value() default {};
}
