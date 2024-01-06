package io.ihankun.framework.mongoplus.strategy.convert.impl;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.mongoplus.annotation.collection.CollectionField;
import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;
import io.ihankun.framework.mongoplus.toolkit.ClassTypeUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author hankun
 * @project mongo-plus
 * @description Collection策略实现类
 * @date 2023-11-02 15:55
 **/
public class CollectionConversionStrategy implements ConversionStrategy<Collection<?>> {

    @Override
    public Collection<?> convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        if (!(fieldValue instanceof Collection<?>)){
            CollectionField collectionField = field.getAnnotation(CollectionField.class);
            if (collectionField != null && collectionField.convertCollect()) {
                Object finalFieldValue = fieldValue;
                fieldValue = new ArrayList<Object>() {{
                    add(finalFieldValue);
                }};
            }
        }
        return JSON.parseArray(JSON.toJSONString(fieldValue), ClassTypeUtil.getListGenericType(field));
    }
}
