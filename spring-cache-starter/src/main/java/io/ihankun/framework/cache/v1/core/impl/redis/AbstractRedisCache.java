package io.ihankun.framework.cache.v1.core.impl.redis;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.cache.v1.comm.RedisDataType;
import io.ihankun.framework.cache.v1.comm.RedisSizeControlMode;
import io.ihankun.framework.cache.v1.config.RedisConfigProperties;
import io.ihankun.framework.cache.v1.holder.RedisTemplateHolder;
import io.ihankun.framework.cache.v1.key.CacheKey;
import io.ihankun.framework.common.exception.BusinessException;
import io.ihankun.framework.common.utils.spring.SpringHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static io.ihankun.framework.cache.v1.comm.CacheErrorCodeEnum.*;

/**
 * @author hankun
 */
@Slf4j
public abstract class AbstractRedisCache {

    protected RedisTemplate getRedisTemplate() {
        return RedisTemplateHolder.ins().getRedisTemplate();
    }

    /**
     * 大小
     */
    protected abstract Long size(CacheKey key);

    /**
     * 数据类型
     */
    protected abstract RedisDataType dataType();


    /**
     * 获取最大过期时间
     */
    protected long getMaxExpireTime() {
        RedisConfigProperties config = SpringHelpers.context().getBean(RedisConfigProperties.class);
        return config.getMaxExpireTime();
    }

    /**
     * 校验kv
     *
     * @param key   缓存key
     * @param value 缓存value
     */
    protected void validate(CacheKey key, Object value, Long expire, TimeUnit timeUnit) {

        if (value == null) {
            return;
        }

        RedisConfigProperties config = null;
        RuntimeException controlException = null;
        try {
            //获取配置
            config = SpringHelpers.context().getBean(RedisConfigProperties.class);

            //是否开启，默认关闭状态
            boolean controlEnable = config.isSizeControlEnable();
            if (controlEnable) {
                sizeControl(config, key, value, expire, timeUnit);
            }

        } catch (RuntimeException e) {
            controlException = e;
            log.error("Redis.sizeControl.exception config={}", JSON.toJSONString(config), e);
        }

        //如果配置不为空，且为限制模式，且检查返回有异常，则阻断流程，其他所有情况均放行
        if (config != null && config.getSizeControlMode().equals(RedisSizeControlMode.LIMIT) && controlException != null) {
            log.error("Redis.sizeControl.LIMIT.exception config={}", JSON.toJSONString(config));
            throw controlException;
        }

    }


    /**
     * 大小限制
     */
    private void sizeControl(RedisConfigProperties config, CacheKey key, Object value, Long expire, TimeUnit timeUnit) {
        //未设置过期时间
        if (expire == null || timeUnit == null) {
            throw BusinessException.build(NOT_SET_EXPIRE_TIME, key.get());
        }


        //超时时间过长
        if (expire.compareTo(timeUnit.convert(config.getMaxExpireTime(), TimeUnit.MINUTES)) > 0) {
            throw BusinessException.build(EXPIRE_TOO_LONG, key.get(), config.getMaxExpireTime() + "分钟");
        }


        //key长度太长
        int keyContentSize = key.get().getBytes().length;
        if (keyContentSize > config.getMaxKeySize()) {
            throw BusinessException.build(KEY_LENGTH_TOO_LONG, key.get(), String.valueOf(config.getMaxKeySize()), String.valueOf(keyContentSize));
        }


        //根据数据类型，判断是否超界
        RedisDataType dataType = dataType();
        Integer controlSize = config.getSizeControlMap().get(dataType);
        if (controlSize != null) {

            switch (dataType) {
                case STRING: {
                    int current = value.toString().length();
                    if (current > controlSize) {
                        throw BusinessException.build(VALUE_LENGTH_TOO_LONG, key.get(), dataType.getValue(), controlSize + "byte", current + "byte");
                    }
                }
                break;
                case LIST: {
                    int current;
                    if (value instanceof List) {
                        current = ((List<?>) value).size();
                    } else {
                        current = 1;
                    }
                    long old = size(key);
                    if (current + old > controlSize) {
                        throw BusinessException.build(VALUE_LENGTH_TOO_LONG, key.get(), dataType.getValue(), String.valueOf(controlSize), String.valueOf(current + old));
                    }
                }
                break;
                case MAP: {
                    int current;
                    if (value instanceof Map) {
                        current = ((Map<?, ?>) value).size();
                    } else {
                        current = 1;
                    }
                    long old = size(key);
                    if (current + old > controlSize) {
                        throw BusinessException.build(VALUE_LENGTH_TOO_LONG, key.get(), dataType.getValue(), String.valueOf(controlSize), String.valueOf(current + old));
                    }
                }
                break;
                case SET: {
                    int current;
                    if (value instanceof Set) {
                        current = ((Set<?>) value).size();
                    } else {
                        current = 1;
                    }
                    long old = size(key);
                    if (current + old > controlSize) {
                        throw BusinessException.build(VALUE_LENGTH_TOO_LONG, key.get(), dataType.getValue(), String.valueOf(controlSize), String.valueOf(current + old));
                    }
                }
                break;
            }
        }
    }
}
