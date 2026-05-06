package io.hankun.framework.ai.tools.client;

import io.hankun.framework.ai.tools.advisors.AdvisorConfig;

/**
 * @description:
 * @className: ClientConfig
 * @createAt: 2025/10/16 16:12
 * @author: hankun
 */
public record ClientConfig(AdvisorConfig advisorConfig) {

    public static ClientConfig merge(ClientConfig clientConfig, ClientConfig override) {
        if (override == null) {
            return clientConfig;
        }
        if (clientConfig == null) {
            return override;
        }
        return new ClientConfig(AdvisorConfig.merge(clientConfig.advisorConfig(), override.advisorConfig()));
    }
}
