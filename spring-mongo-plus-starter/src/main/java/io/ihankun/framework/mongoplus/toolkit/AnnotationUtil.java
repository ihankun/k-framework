package io.ihankun.framework.mongoplus.toolkit;

import io.ihankun.framework.mongoplus.annotation.ID;
import io.ihankun.framework.mongoplus.enums.IdTypeEnum;
import io.ihankun.framework.mongoplus.model.BaseModelID;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hankun
 * 注解操作
 * @since 2023-02-13 13:59
 **/
public class AnnotationUtil {

    public static Map<String,Object> getFieldAnnotation(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        Map<String,Object> resultMap = new HashMap<>();
        Class<?> superclass = object.getClass().getSuperclass();
        IdTypeEnum idTypeEnum = null;
        String fieldName = "id";
        Class<?> fieldType = String.class;
        for (Field field : fields) {
            if (field.isAnnotationPresent(ID.class)){
                idTypeEnum = field.getAnnotation(ID.class).type();
                fieldName = field.getName();
                fieldType = field.getType();
            }
        }
        if (superclass == BaseModelID.class){
            try {
                idTypeEnum = superclass.getField("id").getAnnotation(ID.class).type();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        if (idTypeEnum != null){
            resultMap.put("fieldName",fieldName);
            resultMap.put("fieldType",fieldType);
            resultMap.put("generateType", idTypeEnum);
        }
        return resultMap;
    }
}
