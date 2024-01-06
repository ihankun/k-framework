package io.ihankun.framework.mongoplus.strategy.convert.impl;

import io.ihankun.framework.mongoplus.convert.DocumentMapperConvert;
import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;
import io.ihankun.framework.mongoplus.toolkit.CustomClassUtil;
import org.bson.Document;

import java.lang.reflect.Field;

/**
 * 无策略格式数据
 *
 * @author hankun
 **/
public class DefaultConversionStrategy implements ConversionStrategy<Object> {
    @Override
    public Object convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        if (CustomClassUtil.isCustomObject(field.getType()) && fieldValue.getClass().equals(Document.class)){
            return DocumentMapperConvert.mapDocument((Document) fieldValue,field.getType(),false);
        }
        return fieldValue;
    }
}
