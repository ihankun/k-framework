package io.ihankun.framework.mongoplus.strategy.convert.impl;

import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;

import java.lang.reflect.Field;

/**
 * Float类型转换策略
 *
 * @author hankun
 **/
public class FloatConversionStrategy implements ConversionStrategy<Float> {
    @Override
    public Float convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        return Float.parseFloat(String.valueOf(fieldValue));
    }
}
