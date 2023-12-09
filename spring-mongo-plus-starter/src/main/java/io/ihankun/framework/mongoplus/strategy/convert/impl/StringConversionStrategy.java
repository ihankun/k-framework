package io.ihankun.framework.mongoplus.strategy.convert.impl;

import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;

import java.lang.reflect.Field;

/**
 * String类型转换策略
 *
 * @author hankun
 **/
public class StringConversionStrategy implements ConversionStrategy<String> {
    @Override
    public String convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        return String.valueOf(fieldValue);
    }
}
