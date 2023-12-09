package io.ihankun.framework.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

/**
 * 异步处理
 *
 * @author hankun
 */
@ParametersAreNonnullByDefault
public class AsyncCallback implements Callback {
	private final AsyncExchange exchange;

	AsyncCallback(AsyncExchange exchange) {
		this.exchange = exchange;
	}

	@Override
	public void onFailure(Call call, IOException e) {
		exchange.onFailure(call.request(), e);
	}

	@Override
	public void onResponse(Call call, Response response) throws IOException {
		try (HttpResponse httpResponse = new HttpResponse(response)) {
			exchange.onResponse(httpResponse);
			if (response.isSuccessful()) {
				exchange.onSuccessful(httpResponse);
			} else {
				exchange.onFailure(httpResponse);
			}
		}
	}

}
