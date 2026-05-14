package io.hankun.framework.redis.pubsub;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * redis pub/sub 发布器
 *
 * @author hankun
 */
@Slf4j
@RequiredArgsConstructor
public class RedisPubSubPublisher implements RPubSubPublisher {
	private final StringRedisTemplate redisTemplate;
	private final RedisSerializer<Object> redisSerializer;

	@PostConstruct
	public void init() {
		log.info("RedisPubSubPublisher init success.");
	}

	@Override
	public <T> Long publish(String channel, T message) {
		byte[] channelBytes = redisSerializer.serialize(channel);
		byte[] messageBytes = redisSerializer.serialize(message);
		if (channelBytes == null || messageBytes == null) {
			return 0L;
		}
		return redisTemplate.execute(connection ->
			connection.publish(channelBytes, messageBytes), false);
	}
}
