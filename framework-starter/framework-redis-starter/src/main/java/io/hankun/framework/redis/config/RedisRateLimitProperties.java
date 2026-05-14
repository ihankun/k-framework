package io.hankun.framework.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis 限流配置属性
 *
 * 配置示例：
 * <pre>
 * k.redis.rate.limit:
 *   enable: true
 *   default-max: 100
 *   default-ttl: 1
 *   default-time-unit: MINUTES
 *   white-list:
 *     - /api/health
 *     - /api/public/**
 * </pre>
 *
 * @author hankun
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "k.redis.rate.limit")
@RefreshScope
public class RedisRateLimitProperties {

    /**
     * 是否启用限流，默认 false
     */
    private boolean enable = false;

    /**
     * 全局默认最大请求数，默认 100
     */
    private long defaultMax = 100L;

    /**
     * 全局默认持续时间，默认 1
     */
    private long defaultTtl = 1L;

    /**
     * 全局默认时间单位，默认 MINUTES
     */
    private String defaultTimeUnit = "MINUTES";

    /**
     * 白名单路径列表，支持 Ant 风格通配符
     */
    private List<String> whiteList = new ArrayList<>();
}
