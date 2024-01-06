package io.ihankun.framework.mongoplus.strategy.convert.impl;

import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;

import java.lang.reflect.Field;

/**
 * Long类型转换策略实现
 *
 * @author hankun
 **/
public class LongConversionStrategy implements ConversionStrategy<Long> {
    @Override
    public Long convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        return Long.parseLong(String.valueOf(fieldValue));
    }
}
