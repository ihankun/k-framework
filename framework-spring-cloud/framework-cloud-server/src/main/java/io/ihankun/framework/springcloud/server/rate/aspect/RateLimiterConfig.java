package io.ihankun.framework.springcloud.server.rate.aspect;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author hankun
 */
@Slf4j
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "kun.rate")
public class RateLimiterConfig {

    private boolean enable = true;

}
