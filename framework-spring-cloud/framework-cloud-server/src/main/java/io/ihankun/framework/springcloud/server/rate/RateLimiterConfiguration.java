package io.ihankun.framework.springcloud.server.rate;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties("kun.rate.limiter")
public class RateLimiterConfiguration {

    /**
     * 是否开启，默认开启
     */
    private boolean enabled = true;

    /**
     * 限流器最大数量
     */
    private int limiterMaxSize = 2000;

}
