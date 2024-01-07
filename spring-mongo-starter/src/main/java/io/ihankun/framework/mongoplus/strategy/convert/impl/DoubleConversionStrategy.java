package io.ihankun.framework.mongoplus.strategy.convert.impl;

import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;

import java.lang.reflect.Field;

/**
 * Double类型转换策略
 *
 * @author hankun
 **/
public class DoubleConversionStrategy implements ConversionStrategy<Double> {
    @Override
    public Double convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        return Double.parseDouble(String.valueOf(fieldValue));
    }
}
