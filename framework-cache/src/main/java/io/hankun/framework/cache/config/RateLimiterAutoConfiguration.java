package io.hankun.framework.cache.config;

import io.hankun.framework.cache.ratelimiter.RateLimiterClient;
import io.hankun.framework.cache.ratelimiter.RedisRateLimiterAspect;
import io.hankun.framework.cache.ratelimiter.RedisRateLimiterClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import jakarta.annotation.PostConstruct;

/**
 * 基于 Redis 的分布式限流自动配置
 *
 * 通过配置 k.redis.rate.limit.enable=true 开启限流功能
 *
 * @author hankun
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "k.redis.rate.limit", name = "enable", havingValue = "true")
public class RateLimiterAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("RateLimiterAutoConfiguration initialized, rate limiter enabled");
    }

    /**
     * 加载限流 Lua 脚本
     */
    private RedisScript<Long> redisRateLimiterScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/rate_limiter.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 注册 RedisRateLimiterClient
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StringRedisTemplate.class)
    public RateLimiterClient rateLimiterClient(StringRedisTemplate redisTemplate,
                                                Environment environment) {
        RedisScript<Long> redisRateLimiterScript = redisRateLimiterScript();
        return new RedisRateLimiterClient(redisTemplate, redisRateLimiterScript, environment);
    }

    /**
     * 注册 RedisRateLimiterAspect
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RateLimiterClient.class)
    public RedisRateLimiterAspect rateLimiterAspect(RateLimiterClient rateLimiterClient) {
        return new RedisRateLimiterAspect((RedisRateLimiterClient) rateLimiterClient);
    }
}
