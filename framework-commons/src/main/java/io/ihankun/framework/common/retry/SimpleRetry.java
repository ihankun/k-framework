package io.ihankun.framework.common.retry;

import io.ihankun.framework.common.utils.exception.Exceptions;
import io.ihankun.framework.common.utils.thead.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 简单的 retry 重试
 *
 * @author hankun
 */
@Slf4j
public final class SimpleRetry implements IRetry {
	/**
	 * The default limit to the number of attempts for a new policy.
	 */
	public static final int DEFAULT_MAX_ATTEMPTS = 3;
	/**
	 * Default back off period - 1ms.
	 */
	private static final long DEFAULT_BACK_OFF_PERIOD = 1L;

	/**
	 * 重试次数
	 */
	private final int maxAttempts;
	/**
	 * 重试时间间隔
	 */
	private final long sleepMillis;

	public SimpleRetry() {
		this(DEFAULT_MAX_ATTEMPTS, DEFAULT_BACK_OFF_PERIOD);
	}

	public SimpleRetry(int maxAttempts) {
		this(maxAttempts, DEFAULT_BACK_OFF_PERIOD);
	}

	public SimpleRetry(int maxAttempts, long sleepMillis) {
		this.maxAttempts = maxAttempts;
		this.sleepMillis = (sleepMillis > 0 ? sleepMillis : 1);
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public long getSleepMillis() {
		return sleepMillis;
	}

	@Override
	public <T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback) throws E {
		int retryCount;
		Throwable lastThrowable = null;
		for (int i = 0; i < maxAttempts; i++) {
			try {
				return retryCallback.call();
			} catch (Throwable e) {
				retryCount = i + 1;
				log.warn("retry on {} times error{}.", retryCount, e.getMessage());
				lastThrowable = e;
				if (sleepMillis > 0 && retryCount < maxAttempts) {
					ThreadUtil.sleep(sleepMillis);
				}
			}
		}
		if (lastThrowable == null) {
			lastThrowable = new IOException("retry on " + maxAttempts + " times,still fail.");
		}
		throw Exceptions.unchecked(lastThrowable);
	}

}
