package io.hankun.framework.ai.mcp.app.config;

import io.hankun.framework.ai.core.http.HttpConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @className: KAiHttpConfig
 * @createAt: 2025/5/28 17:11
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "k.ai.http")
public class KAiHttpConfig {
    private String gatewayUrl;
    private String systemId;
    private String token;
    private Boolean enableLoadBalance = false;

    @NestedConfigurationProperty
    private HttpConfig nacos = new HttpConfig();

    @NestedConfigurationProperty
    private HttpConfig client = new HttpConfig();

    private String nacosAddress = "http://localhost:8848/";
    private String nacosUser = "nacos";
    private String nacosPassword = "nacos";
    private String clusterName = "DEFAULT";
    private String groupName = "DEFAULT_GROUP";
    private String namespaceId = "default";
}
