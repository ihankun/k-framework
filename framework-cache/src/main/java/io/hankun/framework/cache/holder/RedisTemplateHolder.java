package io.hankun.framework.cache.holder;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import io.hankun.framework.core.utils.spring.SpringHelpers;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author hankun
 */
public class RedisTemplateHolder {

    private static final AtomicReference<RedisTemplateHolder> INSTANCE = new AtomicReference<>();

    private final AtomicReference<RedisTemplate> redisTemplate = new AtomicReference<>();

    private RedisTemplateHolder() {}

    public static RedisTemplateHolder ins() {
        RedisTemplateHolder currentInstance = INSTANCE.get();
        if (currentInstance == null) {
            synchronized (RedisTemplateHolder.class) {
                currentInstance = INSTANCE.get();
                if (currentInstance == null) {
                    currentInstance = new RedisTemplateHolder();
                    INSTANCE.set(currentInstance);
                }
            }
        }
        return currentInstance;
    }

    public RedisTemplate getRedisTemplate() {
        RedisTemplate localInstance = redisTemplate.get();
        if (localInstance == null) {
            synchronized (RedisTemplateHolder.class) {
                localInstance = redisTemplate.get();
                if (localInstance == null) {
                    RedisTemplate template = SpringHelpers.context().getBean("redisTemplate", RedisTemplate.class);
                    FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
                    template.setKeySerializer(serializer);
                    template.setValueSerializer(serializer);
                    template.setHashKeySerializer(serializer);
                    template.setHashValueSerializer(serializer);
                    redisTemplate.set(template);
                    localInstance = template;
                }
            }
        }
        return localInstance;
    }

    public static void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        ins().redisTemplate.set(redisTemplate);
    }
}
