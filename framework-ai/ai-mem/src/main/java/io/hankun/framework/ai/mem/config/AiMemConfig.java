package io.hankun.framework.ai.mem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @className: AiMemConfig
 * @createAt: 2025/12/3 14:05
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "k.ai.mem")
public class AiMemConfig {
    private String collectName = "aiMem";
    private Integer summaryTopK = 5;
    private Float summaryThreshold = 0.6f;
}
