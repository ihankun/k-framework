package io.ihankun.framework.cache.v2.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * redis key 二次处理，例如：统一添加前缀
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class RedisKeyResolverSerializer implements RedisSerializer<String> {
	private final RedisSerializer<String> redisSerializer;
	private final RedisKeyResolver redisKeyResolver;

	@Override
	public byte[] serialize(String key) throws SerializationException {
		if (redisKeyResolver == null) {
			return redisSerializer.serialize(key);
		} else {
			return redisSerializer.serialize(redisKeyResolver.resolve(key));
		}
	}

	@Override
	public String deserialize(byte[] bytes) throws SerializationException {
		return redisSerializer.deserialize(bytes);
	}

}
