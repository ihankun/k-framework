package io.ihankun.framework.cache.config;

import io.ihankun.framework.cache.ratelimiter.RedisRateLimiterAspect;
import io.ihankun.framework.cache.ratelimiter.RedisRateLimiterClient;
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

/**
 * 基于 redis 的分布式限流自动配置
 *
 * @author hankun
 */
@Configuration
@ConditionalOnProperty(value = "redis.config.rate-limiter.enable")
public class RateLimiterAutoConfiguration {

	private RedisScript<Long> redisRateLimiterScript() {
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/rate_limiter.lua")));
		redisScript.setResultType(Long.class);
		return redisScript;
	}

	@Bean
	@ConditionalOnMissingBean
	public RedisRateLimiterClient redisRateLimiter(StringRedisTemplate redisTemplate,
												   Environment environment) {
		RedisScript<Long> redisRateLimiterScript = redisRateLimiterScript();
		return new RedisRateLimiterClient(redisTemplate, redisRateLimiterScript, environment);
	}

	@Bean
	@ConditionalOnMissingBean
	public RedisRateLimiterAspect redisRateLimiterAspect(RedisRateLimiterClient rateLimiterClient) {
		return new RedisRateLimiterAspect(rateLimiterClient);
	}
}
