package io.ihankun.framework.common.http.v2;

import okhttp3.logging.HttpLoggingInterceptor;

import javax.annotation.Nonnull;

/**
 * OkHttp console log.
 *
 * @author hankun
 */
public enum HttpConsoleLogger implements HttpLoggingInterceptor.Logger {
	/**
	 * 实例
	 */
	INSTANCE;

	public void log(@Nonnull String message) {
		// 统一添加前缀，方便在茫茫日志中查看
		System.out.printf("HttpLogger: %s\n", message);
	}

}
