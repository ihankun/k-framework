package io.ihankun.framework.common.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.annotation.Nonnull;

/**
 * OkHttp logger, Slf4j and console log.
 *
 * @author hankun
 */
@Slf4j
public enum HttpLogger implements HttpLoggingInterceptor.Logger {

	/**
	 * http 日志：Slf4j
	 */
	Slf4j() {
		@Override
		public void log(@Nonnull String message) {
			log.info(message);
		}
	},

	/**
	 * http 日志：Console
	 */
	Console() {
		@Override
		public void log(@Nonnull String message) {
			// 统一添加前缀，方便在茫茫日志中查看
			System.out.printf("HttpLogger: %s\n", message);
		}
	};

}
