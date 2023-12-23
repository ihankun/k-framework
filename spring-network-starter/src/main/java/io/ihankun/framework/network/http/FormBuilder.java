package io.ihankun.framework.network.http;

import okhttp3.FormBody;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 表单构造器
 *
 * @author hankun
 */
public class FormBuilder {
	private final HttpRequest request;
	private final FormBody.Builder formBuilder;

	FormBuilder(HttpRequest request) {
		this.request = request;
		this.formBuilder = new FormBody.Builder();
	}

	public FormBuilder add(String name, @Nullable Object value) {
		this.formBuilder.add(name, HttpRequest.handleValue(value));
		return this;
	}

	public FormBuilder addMap(@Nullable Map<String, Object> formMap) {
		if (formMap != null && !formMap.isEmpty()) {
			formMap.forEach(this::add);
		}
		return this;
	}

	public FormBuilder addEncoded(String name, @Nullable Object encodedValue) {
		this.formBuilder.addEncoded(name, HttpRequest.handleValue(encodedValue));
		return this;
	}

	public FormBuilder add(Consumer<FormBody.Builder> consumer) {
		consumer.accept(this.formBuilder);
		return this;
	}

	public HttpRequest build() {
		return this.request.form(this.formBuilder.build());
	}

	public Exchange execute() {
		return this.build().execute();
	}

	public AsyncExchange async() {
		return this.build().async();
	}
}
