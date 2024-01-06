package io.ihankun.framework.mongoplus.strategy.convert.impl;

import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;

import java.lang.reflect.Field;

/**
 * Integer转换策略实现
 *
 * @author hankun
 **/
public class IntegerConversionStrategy implements ConversionStrategy<Integer> {
    @Override
    public Integer convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        return Integer.parseInt(String.valueOf(fieldValue));
    }
}
