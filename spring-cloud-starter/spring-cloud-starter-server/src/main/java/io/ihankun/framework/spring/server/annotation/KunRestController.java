package io.ihankun.framework.spring.server.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * @author hankun
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
public @interface KunRestController {

    /**
     * value值定义，指向RestController.value
     *
     * @return
     */
    @AliasFor(annotation = RestController.class)
    String value() default "";
}
