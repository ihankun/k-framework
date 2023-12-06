package io.ihankun.framework.captcha.v2.annotation;

import io.ihankun.framework.common.constants.captcha.CaptchaTypeConstant;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface Captcha {

    @AliasFor("type")
    String value() default CaptchaTypeConstant.SLIDER;

    @AliasFor("value")
    String type() default CaptchaTypeConstant.SLIDER;
}
