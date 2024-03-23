package io.ihankun.framework.mongoplus.strategy.convert.impl;

import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * BigDecimal类型转换器策略实现类
 *
 * @author hankun
 **/
public class BigDecimalConversionStrategy implements ConversionStrategy<BigDecimal> {

    private final Logger logger = LoggerFactory.getLogger(BigDecimalConversionStrategy.class);

    @Override
    public BigDecimal convertValue(Field field, Object obj, Object fieldValue) throws IllegalAccessException {
        try {
            return new BigDecimal(String.valueOf(fieldValue));
        }catch (Exception e){
            logger.error("Convert fieldValue To BigDecimal Fail,Exception Message: {}",e.getMessage(),e);
        }
        return null;
    }
}
