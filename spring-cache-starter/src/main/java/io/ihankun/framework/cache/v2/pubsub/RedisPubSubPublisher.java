package io.ihankun.framework.cache.v2.pubsub;

import io.ihankun.framework.cache.v2.cache.MicaRedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redisson pub/sub 发布器
 *
 * @author hankun
 */
@Slf4j
@RequiredArgsConstructor
public class RedisPubSubPublisher implements InitializingBean, RPubSubPublisher {
	private final MicaRedisCache redisCache;
	private final RedisSerializer<Object> redisSerializer;

	@Override
	public <T> Long publish(String channel, T message) {
		return redisCache.publish(channel, message, redisSerializer::serialize);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("RPubSubPublisher init success.");
	}
}
