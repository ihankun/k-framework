package io.ihankun.framework.cache.resolver;

import io.ihankun.framework.cache.config.RedisConfigProperties;
import io.ihankun.framework.common.v1.utils.string.CharPool;
import io.ihankun.framework.common.v1.utils.string.StringUtil;
import lombok.RequiredArgsConstructor;

/**
 * 默认的 key 处理，添加配置的 key 前缀
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class DefaultRedisKeyResolver implements RedisKeyResolver {
	private final RedisConfigProperties properties;

	@Override
	public String resolve(String key) {
		String keyPrefix = properties.getKeyPrefix();
		return StringUtil.isBlank(keyPrefix) ? key : keyPrefix + CharPool.COLON + key;
	}

}
