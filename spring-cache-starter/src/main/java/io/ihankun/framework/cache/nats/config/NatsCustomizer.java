package io.ihankun.framework.cache.nats.config;

import io.nats.client.Options;

/**
 * nats Options Builder 自定义配置
 *
 * @author hankun
 */
@FunctionalInterface
public interface NatsCustomizer {

	/**
	 * Customize the Options Builder.
	 *
	 * @param builder the Options Builder to customize
	 */
	void customize(Options.Builder builder);

}
