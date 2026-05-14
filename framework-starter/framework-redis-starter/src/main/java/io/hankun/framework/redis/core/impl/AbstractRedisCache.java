package io.hankun.framework.redis.core.impl;

import com.alibaba.fastjson.JSON;
import io.hankun.framework.redis.config.RedisConfigProperties;
import io.hankun.framework.redis.enums.RedisDataType;
import io.hankun.framework.redis.enums.RedisSizeControlMode;
import io.hankun.framework.redis.holder.RedisTemplateHolder;
import io.hankun.framework.redis.key.CacheKey;
import io.hankun.framework.core.exception.BusinessException;
import io.hankun.framework.core.utils.spring.SpringHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.hankun.framework.redis.error.CacheErrorCodeEnum.*;

/**
 * @author hankun
 */
@Slf4j
public abstract class AbstractRedisCache {

    protected RedisTemplate getRedisTemplate() {
        return RedisTemplateHolder.ins().getRedisTemplate();
    }

//    /**
//     * 大小
//     */
//    protected abstract Long size(ICacheKey key);

    /**
     * 数据类型
     */
    protected abstract RedisDataType dataType();

    /**
     * 大小
     */
    protected abstract long getSizeInternal(String key);


    /**
     * 批量删除
     *
     * @param keys 缓存key集合
     * @return boolean
     */
    public boolean batchDel(Collection<? extends CacheKey> keys) {
        int batchSize = SpringHelpers.context().getBean(RedisConfigProperties.class).getBatchSize();
        List<? extends List<? extends CacheKey>> split = split(keys, batchSize);
        try {
            split.forEach(batchList -> {
                List<String> collect = batchList.stream().map(CacheKey::get).collect(Collectors.toList());
                getRedisTemplate().delete(collect);
            });
        } catch (Exception e) {
            log.error("batchDel error: {}", e.getMessage());
            return false;
        }
        return true;
    }

    public static <T> List<List<T>> split(Collection<T> collection, int size) {
        List<List<T>> result = new ArrayList<>();
        ArrayList<T> subList = new ArrayList<>(size);
        T t;
        for (Iterator<T> var4 = collection.iterator(); var4.hasNext(); subList.add(t)) {
            t = var4.next();
            if (subList.size() >= size) {
                result.add(subList);
                subList = new ArrayList<>(size);
            }
        }
        result.add(subList);
        return result;
    }

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
    protected void validate(String key, Object value, Long expire, TimeUnit timeUnit) {

        if (value == null) {
            return;
        }

        RedisConfigProperties config = null;
        RuntimeException controlException = null;
        try {
            //获取配置
            config = SpringHelpers.context().getBean(RedisConfigProperties.class);

            //是否开启，默认关闭状态
            boolean controlEnable = config.isEnable();
            if (controlEnable) {
                sizeControl(config, key, value, expire, timeUnit);
            }

        } catch (RuntimeException e) {
            controlException = e;
            log.error("Redis.sizeControl.exception config={}", JSON.toJSONString(config), e);
        }

        //如果配置不为空，且为限制模式，且检查返回有异常，则阻断流程，其他所有情况均放行
        if (config != null && config.getMode().equals(RedisSizeControlMode.LIMIT) && controlException != null) {
            log.error("Redis.sizeControl.LIMIT.exception config={}", JSON.toJSONString(config));
            throw controlException;
        }

    }


    /**
     * 大小限制
     */
    private void sizeControl(RedisConfigProperties config, String key, Object value, Long expire, TimeUnit timeUnit) {
        //未设置过期时间
        if (expire == null || timeUnit == null) {
            throw BusinessException.build(NOT_SET_EXPIRE_TIME, key);
        }


        //超时时间过长
        if (expire.compareTo(timeUnit.convert(config.getMaxExpireTime(), TimeUnit.MINUTES)) > 0) {
            throw BusinessException.build(EXPIRE_TOO_LONG, key, config.getMaxExpireTime() + "分钟");
        }


        //key长度太长
        int keyContentSize = key.getBytes().length;
        if (keyContentSize > config.getMaxKeySize()) {
            throw BusinessException.build(KEY_LENGTH_TOO_LONG, key, String.valueOf(config.getMaxKeySize()), String.valueOf(keyContentSize));
        }


        //根据数据类型，判断是否超界
        RedisDataType dataType = dataType();
        Integer controlSize = config.getSizeControlMap().get(dataType);
        if (controlSize != null) {
            int currentSize = getCurrentSize(value, dataType);
            long oldSize = getSizeInternal(key);
            if (currentSize + oldSize > controlSize) {
                throw BusinessException.build(VALUE_LENGTH_TOO_LONG,
                        key,
                        dataType.getValue(),
                        String.valueOf(controlSize),
                        String.valueOf(currentSize + oldSize));
            }
        }
    }

    private int getCurrentSize(Object value, RedisDataType dataType) {
        switch (dataType) {
            case STRING:
                return String.valueOf(value).length();
            case SET:
                return (value instanceof Set) ? ((Set<?>) value).size() : 1;
            case LIST:
                return (value instanceof List) ? ((List<?>) value).size() : 1;
            case MAP:
                return (value instanceof Map) ? ((Map<?, ?>) value).size() : 1;
            default:
                return 1;
        }
    }


}
