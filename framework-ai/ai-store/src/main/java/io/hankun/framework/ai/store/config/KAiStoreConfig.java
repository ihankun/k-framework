package io.hankun.framework.ai.store.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @className: MsunAiStoreConfig
 * @createAt: 2025/10/16 16:24
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "msun.ai.store")
public class KAiStoreConfig {

    private Integer memoryExpireTimeHours = 24;

    private Integer maxMemorySize = 10;

    private Integer taskHistoryCount = 5;
}
