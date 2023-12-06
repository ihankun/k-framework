package io.ihankun.framework.cache.v2.resolver;

/**
 * redis 命名空间隔离
 *
 * @author hankun
 */
public interface RedisKeyResolver {

	/**
	 * 用于自定义“租户”等隔离，默认直接拼接
	 *
	 * @param key 名称
	 * @return 处理后的名称
	 */
	String resolve(String key);

}
