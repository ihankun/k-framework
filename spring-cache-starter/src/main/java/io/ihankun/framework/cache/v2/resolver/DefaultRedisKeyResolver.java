package io.ihankun.framework.cache.v2.resolver;

import io.ihankun.framework.cache.v2.config.MicaRedisProperties;
import io.ihankun.framework.common.utils.string.CharPool;
import io.ihankun.framework.common.utils.string.StringUtil;
import lombok.RequiredArgsConstructor;

/**
 * 默认的 key 处理，添加配置的 key 前缀
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class DefaultRedisKeyResolver implements RedisKeyResolver {
	private final MicaRedisProperties properties;

	@Override
	public String resolve(String key) {
		String keyPrefix = properties.getKeyPrefix();
		return StringUtil.isBlank(keyPrefix) ? key : keyPrefix + CharPool.COLON + key;
	}

}
