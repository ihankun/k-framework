package io.ihankun.framework.http;

import okhttp3.Call;
import okhttp3.Request;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 异步执行器
 *
 * @author hankun
 */
@ParametersAreNonnullByDefault
public class AsyncExchange {
	private final Call call;
	@Nullable
	private Consumer<ResponseSpec> successConsumer;
	@Nullable
	private Consumer<ResponseSpec> responseConsumer;
	@Nullable
	private BiConsumer<Request, HttpException> failedBiConsumer;

	AsyncExchange(Call call) {
		this.call = call;
		this.successConsumer = null;
		this.responseConsumer = null;
		this.failedBiConsumer = null;
	}

	public void onSuccessful(Consumer<ResponseSpec> consumer) {
		this.successConsumer = consumer;
		this.execute();
	}

	public void onResponse(Consumer<ResponseSpec> consumer) {
		this.responseConsumer = consumer;
		this.execute();
	}

	public AsyncExchange onFailed(BiConsumer<Request, HttpException> biConsumer) {
		this.failedBiConsumer = biConsumer;
		return this;
	}

	private void execute() {
		call.enqueue(new AsyncCallback(this));
	}

	protected void onResponse(HttpResponse response) {
		if (responseConsumer != null) {
			responseConsumer.accept(response);
		}
	}

	protected void onSuccessful(HttpResponse response) {
		if (successConsumer != null) {
			successConsumer.accept(response);
		}
	}

	protected void onFailure(Request request, IOException e) {
		if (failedBiConsumer != null) {
			failedBiConsumer.accept(request, new HttpException(request, e));
		}
	}

	protected void onFailure(HttpResponse response) {
		if (failedBiConsumer != null) {
			failedBiConsumer.accept(response.rawRequest(), new HttpException(response));
		}
	}

}
