package io.ihankun.framework.springcloud.api.validator.annotation;


import io.ihankun.framework.springcloud.api.validator.CheckCaseValidator;
import io.ihankun.framework.springcloud.api.validator.enums.CaseMode;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author hankun
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CheckCaseValidator.class})
@Documented
public @interface CheckCase {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    CaseMode value();
}
