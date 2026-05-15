package io.hankun.framework.cache.ratelimiter;

import io.hankun.framework.core.utils.string.CharPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis 限流服务
 *
 * @author dream.lu
 */
@Slf4j
@RequiredArgsConstructor
public class RedisRateLimiterClient implements RateLimiterClient {
	/**
	 * redis 限流 key 前缀
	 */
	private static final String REDIS_KEY_PREFIX = "limiter:";
	/**
	 * 失败的默认返回值
	 */
	private static final long FAIL_CODE = 0;
	/**
	 * redisTemplate
	 */
	private final StringRedisTemplate redisTemplate;
	/**
	 * redisScript
	 */
	private final RedisScript<Long> script;
	/**
	 * env
	 */
	private final Environment environment;

	@Override
	public boolean isAllowed(String key, long max, long ttl, TimeUnit timeUnit) {
		// redis key
		String redisKeyBuilder = REDIS_KEY_PREFIX +
			getApplicationName(environment) + CharPool.COLON + key;
		List<String> keys = Collections.singletonList(redisKeyBuilder);
		// 转为毫秒
		long ttlMillis = timeUnit.toMillis(ttl);
		// 执行命令
		Long result = this.redisTemplate.execute(this.script, keys, Long.toString(max), Long.toString(ttlMillis));
		boolean allowed = result != null && result != FAIL_CODE;
		if (!allowed) {
			log.warn("Rate limit exceeded, key={}, max={}, ttl={} {}", key, max, ttl, timeUnit.name().toLowerCase());
		}
		return allowed;
	}

	private static String getApplicationName(Environment environment) {
		return environment.getProperty("spring.application.name", "");
	}

}
