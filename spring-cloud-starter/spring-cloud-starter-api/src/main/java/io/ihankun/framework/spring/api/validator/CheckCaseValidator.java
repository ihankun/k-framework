package io.ihankun.framework.spring.api.validator;


import io.ihankun.framework.spring.api.validator.annotation.CheckCase;
import io.ihankun.framework.spring.api.validator.enums.CaseMode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author hankun
 */
public class CheckCaseValidator implements ConstraintValidator<CheckCase, String> {

    /**
     * 枚举
     */
    private CaseMode caseMode;

    @Override
    public void initialize(CheckCase checkCase) {
        this.caseMode = checkCase.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        if (caseMode == CaseMode.UPPER) {
            return s.equals(s.toUpperCase());
        } else {
            return s.equals(s.toLowerCase());
        }
    }
}
