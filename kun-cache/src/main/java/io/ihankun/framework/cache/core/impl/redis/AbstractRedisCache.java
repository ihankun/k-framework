package io.ihankun.framework.cache.core.impl.redis;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import io.ihankun.framework.cache.RedisDataType;
import io.ihankun.framework.cache.key.CacheKey;
import io.ihankun.framework.common.exception.BusinessException;
import io.ihankun.framework.common.utils.SpringHelpers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public abstract class AbstractRedisCache {

    /**
     * redis template
     */
    private static RedisTemplate redisTemplate;
    //private RedisSizeCheckProperties sizeCheckProperties;

    /**
     * 缓存最长有效时间
     * 缓存默认有效时间
     */
    protected static final Long MAX_EXPIRE = 3L;

    /**
     * 缓存KEY最大长度
     */
    private static final int MAX_CACHE_KEY_SIZE = 256;

    /**
     * 缓存VALUE最大长度
     */
    private static final int MAX_CACHE_VALUE_SIZE = 1024 * 1024;


    protected RedisTemplate getRedisTemplate() {
        if (redisTemplate != null) {
            return redisTemplate;
        }

        synchronized (this) {
            if (redisTemplate != null) {
                return redisTemplate;
            }

            RedisTemplate template = SpringHelpers.context().getBean("redisTemplate", RedisTemplate.class);
            FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
            template.setKeySerializer(serializer);
            template.setValueSerializer(serializer);
            template.setHashKeySerializer(serializer);
            template.setHashValueSerializer(serializer);
            redisTemplate = template;

            //sizeCheckProperties = SpringHelpers.context().getBean("RuleRedisSizeCheckProperties", RedisSizeCheckProperties.class);
        }
        return redisTemplate;
    }

    /**
     * 大小
     *
     * @param key
     * @return
     * @throws BusinessException
     */
    protected abstract Long size(CacheKey key);

    /**
     * 数据类型
     *
     * @return
     */
    protected abstract RedisDataType dataType();


    /**
     * 校验kv
     *
     * @param key   缓存key
     * @param value 缓存value
     */
    protected void validate(CacheKey key, Object value, Long expire, TimeUnit timeUnit) {
        if (expire == null || timeUnit == null) {
            throw BusinessException.build("cache", "003", "cache.key.size.not.allowed.no_expire,key:" + key.get());
        }

        if (expire.compareTo(timeUnit.convert(MAX_EXPIRE, TimeUnit.DAYS)) > 0) {
            throw BusinessException.build("cache", "004", "cache.key.size.not.allowed.greater.than.MAX_EXPIRE,timeUnit:" + timeUnit + ",expire:" + expire + ",MAX_EXPIRE:" + MAX_EXPIRE);
        }

        int keyContentSize = key.get().getBytes().length;
        if (keyContentSize > MAX_CACHE_KEY_SIZE) {
            throw BusinessException.build("cache", "001", "cache.key.size.not.allowed.greater.than.MAX_CACHE_KEY_SIZE,keySize:" + keyContentSize + ",MAX_CACHE_KEY_SIZE:" + MAX_CACHE_KEY_SIZE);
        }
        if (value == null) {
            return;
        }

        int valueContentSize = value.toString().getBytes().length;
        if (valueContentSize > MAX_CACHE_VALUE_SIZE) {
            throw BusinessException.build("cache", "002", "cache.value.size.not.allowed.greater.than.MAX_CACHE_VALUE_SIZE,valueSize:" + valueContentSize + ",MAX_CACHE_VALUE_SIZE:" + MAX_CACHE_VALUE_SIZE);
        }

        //检查实际大小
        //checkSize(key);
    }

    /**
     * 检查value大小
     * 防止出现超大的value
     *
     * @param key
     */
//    private void checkSize(CacheKey key) {
//        if(RedisDataType.STRING.equals(dataType())){
//            return;
//        }
//        if (sizeCheckProperties == null || !sizeCheckProperties.isEnabled() || CollectionUtils.isEmpty(sizeCheckProperties.getConfigs())) {
//            return;
//        }
//        if (!sizeCheckProperties.getConfigs().containsKey(dataType().getValue())) {
//            return;
//        }
//
//        Long size = size(key);
//        if (size == null || size == 0) {
//            return;
//        }
//        if (size.compareTo(sizeCheckProperties.getConfigs().get(dataType().getValue())) > 0) {
//            throw BusinessException.build("cache", "005", "cache.value.actual.size.greater.than.MAX_CACHE_VALUE_SIZE,valueSize:" + size + ",MAX_CACHE_VALUE_SIZE:" + sizeCheckProperties.getConfigs().get(dataType().getValue()));
//        }
//
//    }
}
