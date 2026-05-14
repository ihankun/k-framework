package io.hankun.framework.redis.ratelimiter;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * 限流异常
 *
 * @author hankun
 */
@Getter
public class RateLimiterException extends RuntimeException {
	private final String key;
	private final long max;
	private final long ttl;
	private final TimeUnit timeUnit;

	public RateLimiterException(String key, long max, long ttl, TimeUnit timeUnit) {
		super("您的访问次数已超限：" + key + "，速率：" + max + "/" + timeUnit.toSeconds(ttl) + "s");
		this.key = key;
		this.max = max;
		this.ttl = ttl;
		this.timeUnit = timeUnit;
	}
}
