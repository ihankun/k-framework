package io.ihankun.framework.common.http;

import io.ihankun.framework.common.retry.IRetry;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.function.Predicate;

/**
 * 重试拦截器，应对代理问题
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class RetryInterceptor implements Interceptor {
	private final IRetry retry;
	@Nullable
	private final Predicate<ResponseSpec> respPredicate;

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		return retry.execute(() -> {
			Response response = chain.proceed(request);
			// 结果集校验
			if (respPredicate == null) {
				return response;
			}
			// copy 一份 body
			ResponseBody body = response.peekBody(Long.MAX_VALUE);
			try (HttpResponse httpResponse = new HttpResponse(response)) {
				if (respPredicate.test(httpResponse)) {
					throw new IOException("Http Retry ResponsePredicate test Failure.");
				}
			}
			return response.newBuilder().body(body).build();
		});
	}

}
