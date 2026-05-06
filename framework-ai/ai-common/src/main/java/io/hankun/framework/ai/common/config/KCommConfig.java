package io.hankun.framework.ai.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @className: KCommConfig
 * @createAt: 2025/7/31 15:19
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "k.ai.common")
public class KCommConfig {

    @Value("${spring.application.name}")
    private String serviceName;
}
