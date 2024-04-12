package io.ihankun.framework.poi.util;

import io.ihankun.framework.poi.annotation.Excel;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * HIBERNATE 校验工具类
 *
 * @author hankun
 */
public class PoiValidationUtil {

    private final static Validator VALIDATOR;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    public static String validation(Object obj, Class[] verfiyGroup) {
        Set<ConstraintViolation<Object>> set = null;
        if(verfiyGroup != null){
            set = VALIDATOR.validate(obj,verfiyGroup);
        }else{
            set = VALIDATOR.validate(obj);
        }
        if (set!= null && set.size() > 0) {
            return getValidateErrMsg(set);
        }
        return null;
    }

    private static String getValidateErrMsg(Set<ConstraintViolation<Object>> set) {
        StringBuilder builder = new StringBuilder();
        for (ConstraintViolation<Object> constraintViolation : set) {
            Class cls = constraintViolation.getRootBean().getClass();
            String fieldName = constraintViolation.getPropertyPath().toString();
            List<Field> fields = new ArrayList<>(Arrays.asList(cls.getDeclaredFields()));
            Class superClass = cls.getSuperclass();
            if (superClass != null) {
                fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            }
            String name = null;
            for (Field field: fields) {
                if (field.getName().equals(fieldName) && field.isAnnotationPresent(Excel.class)) {
                    name = field.getAnnotation(Excel.class).name();
                    break;
                }
            }
            if (name == null) {
               name = fieldName;
            }
            builder.append(name).append(constraintViolation.getMessage()).append(",");
        }
        return builder.substring(0, builder.length() - 1);
    }

}
