package io.ihankun.framework.cache.nats.config;

import io.nats.client.api.StreamConfiguration;

/**
 * nats stream StreamConfiguration 自定义配置
 *
 * @author hankun
 */
@FunctionalInterface
public interface NatsStreamCustomizer {

    /**
     * Customize the StreamConfiguration.
     *
     * @param builder the StreamConfiguration Builder configuration to customize
     */
    void customize(StreamConfiguration.Builder builder);

}
