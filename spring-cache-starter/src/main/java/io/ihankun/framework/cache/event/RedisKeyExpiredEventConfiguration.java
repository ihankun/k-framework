package io.ihankun.framework.cache.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * redis key 失效事件
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
