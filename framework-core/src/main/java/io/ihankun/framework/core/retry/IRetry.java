package io.ihankun.framework.core.retry;


/**
 * 重试接口
 *
 * @author hankun
 */
public interface IRetry {

	<T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback) throws E;

}
