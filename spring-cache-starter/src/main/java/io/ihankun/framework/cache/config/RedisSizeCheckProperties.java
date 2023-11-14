package io.ihankun.framework.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author hankun
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "kun.rule.redis.size.check")
public class RedisSizeCheckProperties {

    /**
     * 是否开启
     */
    private boolean enabled = false;
    /**
     * 配置
     */
    private Map<String, Long> configs;
}
