package io.ihankun.framework.cache.v2.ratelimiter;

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
//		super("您的访问次数已超限：%s，速率：%d/%ds".formatted(key, max, timeUnit.toSeconds(ttl)));
		this.key = key;
		this.max = max;
		this.ttl = ttl;
		this.timeUnit = timeUnit;
	}
}
