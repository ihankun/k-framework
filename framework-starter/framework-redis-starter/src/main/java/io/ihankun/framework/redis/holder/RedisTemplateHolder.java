package io.ihankun.framework.redis.holder;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import io.ihankun.framework.core.utils.spring.SpringHelpers;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author hankun
 */
public class RedisTemplateHolder {

    private static class RedisTemplateHolderHolder{
        private static final RedisTemplateHolder INSTANCE = new RedisTemplateHolder();
    }

    private volatile RedisTemplate redisTemplate;

    private RedisTemplateHolder() {}

    public static RedisTemplateHolder ins() {
        return RedisTemplateHolderHolder.INSTANCE;
    }

    public RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            synchronized (RedisTemplateHolder.class) {
                if (redisTemplate == null) {
                    RedisTemplate template = SpringHelpers.context().getBean("redisTemplate", RedisTemplate.class);
                    FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
                    template.setKeySerializer(serializer);
                    template.setValueSerializer(serializer);
                    template.setHashKeySerializer(serializer);
                    template.setHashValueSerializer(serializer);
                    redisTemplate = template;
                }
            }
        }
        return redisTemplate;
    }

    public static void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        RedisTemplateHolder.ins().redisTemplate = redisTemplate;
    }
}
