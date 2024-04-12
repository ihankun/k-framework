package io.ihankun.framework.spring.api.validator.annotation;


import io.ihankun.framework.spring.api.validator.CheckCaseValidator;
import io.ihankun.framework.spring.api.validator.enums.CaseMode;

import javax.validation.Constraint;
import javax.validation.Payload;
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
