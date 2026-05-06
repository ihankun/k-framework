package io.hankun.framework.ai.model.config;

import io.hankun.framework.ai.common.http.HttpConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @className: MsunRerankHttpConfig
 * @createAt: 2025/7/4 11:34
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "msun.ai.rerank.http")
public class MsunRerankHttpConfig {

    private String baseUrl;

    private String apiKey;

    private String model;

    @NestedConfigurationProperty
    private HttpConfig client = new HttpConfig();
}
