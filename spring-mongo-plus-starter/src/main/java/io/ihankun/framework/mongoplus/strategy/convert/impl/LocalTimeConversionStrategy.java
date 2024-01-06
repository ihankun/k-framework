package io.ihankun.framework.mongoplus.strategy.convert.impl;

import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;
import io.ihankun.framework.mongoplus.toolkit.InstantUtil;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author hankun
 * @project mongo-plus
 * @description LocalTime转换策略实现类
 * @date 2023-10-17 10:02
 **/
public class LocalTimeConversionStrategy implements ConversionStrategy<LocalTime> {
    @Override
    public LocalTime convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        return fieldValue.getClass().equals(Long.class) ? InstantUtil.convertTimestampToLocalTime((Long) fieldValue) : InstantUtil.convertTimestampToLocalTime(((Date) fieldValue).toInstant());
    }
}
