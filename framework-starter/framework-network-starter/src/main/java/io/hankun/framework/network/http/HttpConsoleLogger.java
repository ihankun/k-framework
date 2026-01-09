package io.hankun.framework.network.http;

import jakarta.annotation.Nonnull;
import okhttp3.logging.HttpLoggingInterceptor;


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
