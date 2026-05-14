package io.hankun.framework.redis.config;

import io.hankun.framework.redis.pubsub.RPubSubListenerDetector;
import io.hankun.framework.redis.pubsub.RPubSubPublisher;
import io.hankun.framework.redis.pubsub.RedisPubSubPublisher;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis Pub/Sub 自动配置
 *
 * 通过配置 k.redis.pubsub.enable=true 开启
 *
 * @author hankun
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisMessageListenerContainer.class)
@ConditionalOnProperty(prefix = "k.redis.pubsub", name = "enable", havingValue = "true")
public class PubSubAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("PubSubAutoConfiguration initialized, redis pub/sub enabled");
    }

    /**
     * 注册消息发布器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StringRedisTemplate.class)
    public RPubSubPublisher rPubSubPublisher(StringRedisTemplate redisTemplate,
                                              RedisSerializer<Object> redisSerializer) {
        return new RedisPubSubPublisher(redisTemplate, redisSerializer);
    }

    /**
     * 注册监听器检测器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisMessageListenerContainer.class)
    public RPubSubListenerDetector rPubSubListenerDetector(
            RedisMessageListenerContainer redisMessageListenerContainer,
            RedisSerializer<Object> redisSerializer) {
        return new RPubSubListenerDetector(redisMessageListenerContainer, redisSerializer);
    }
}
