package io.hankun.framework.cache.resolver;

import io.hankun.framework.core.utils.string.CharPool;
import io.hankun.framework.core.utils.string.StringUtil;
import io.hankun.framework.redis.config.RedisSizeProperties;
import lombok.RequiredArgsConstructor;

/**
 * 默认的 key 处理，添加配置的 key 前缀
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class DefaultRedisKeyResolver implements RedisKeyResolver {
	private final RedisSizeProperties properties;

	@Override
	public String resolve(String key) {
		String keyPrefix = properties.getKeyPrefix();
		return StringUtil.isBlank(keyPrefix) ? key : keyPrefix + CharPool.COLON + key;
	}

}
