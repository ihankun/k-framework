package io.hankun.framework.mongoplus.strategy.convert.impl;

import io.hankun.framework.mongoplus.strategy.convert.ConversionStrategy;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author hankun
 * @project mongo-plus
 * @description Date类型转换器策略实现
 * @date 2023-10-17 10:40
 **/
public class DateConversionStrategy implements ConversionStrategy<Date> {

    @Override
    public Date convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        Date date;
        if (fieldValue.getClass().equals(Long.class)){
            date = new Date((Long) fieldValue);
        }else {
            date = (Date) fieldValue;
        }
        return date;
    }
}
