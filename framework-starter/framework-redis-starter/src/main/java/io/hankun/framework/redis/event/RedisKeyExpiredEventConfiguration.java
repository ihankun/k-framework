package io.hankun.framework.redis.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis key 失效事件配置
 * <p>
 * 启用后可以监听 Redis key 过期事件，需配置 {@code redis.config.key-expired-event.enable=true}。
 *
 * @author hankun
 */
@Configuration
@ConditionalOnProperty(value = "redis.config.key-expired-event.enable")
public class RedisKeyExpiredEventConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KeyExpirationEventMessageListener keyExpirationEventMessageListener(RedisMessageListenerContainer listenerContainer) {
        return new KeyExpirationEventMessageListener(listenerContainer);
    }

}
