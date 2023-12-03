package io.ihankun.framework.captcha;

import io.ihankun.framework.captcha.v2.store.CacheStore;
import io.ihankun.framework.captcha.v2.store.impl.LocalCacheStore;
import io.ihankun.framework.captcha.v2.store.impl.RedisCacheStore;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfigureAfter({RedisAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
public class CacheStoreAutoConfiguration {

    /**
     * RedisCacheStoreConfiguration
     *
     * @author hankun
     * @since 2020/10/27 14:06
     */
    @Order(1)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(StringRedisTemplate.class)
    public static class RedisCacheStoreConfiguration {

        @Bean
        @ConditionalOnBean(StringRedisTemplate.class)
        @ConditionalOnMissingBean(CacheStore.class)
        public CacheStore redis(StringRedisTemplate redisTemplate) {
            return new RedisCacheStore(redisTemplate);
        }

    }

    /**
     * LocalCacheStoreConfiguration
     *
     * @author hankun
     * @since 2020/10/27 14:06
     */
    @Order(2)
    @Configuration(proxyBeanMethods = false)
    public static class LocalCacheStoreConfiguration {

        @Bean
        @ConditionalOnMissingBean(CacheStore.class)
        public CacheStore local() {
            return new LocalCacheStore();
        }

    }
}
