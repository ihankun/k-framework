package io.ihankun.framework.http;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

/**
 * HttpException
 *
 * @author hankun
 */
@ParametersAreNonnullByDefault
public class HttpException extends IOException {
	private final ResponseSpec response;

	HttpException(ResponseSpec response) {
		super(response.toString());
		this.response = response;
	}

	HttpException(Request request, Throwable cause) {
		super(cause);
		this.response = getResponse(request, cause.getMessage());
	}

	public ResponseSpec getResponse() {
		return response;
	}

	@Override
	public Throwable fillInStackTrace() {
		Throwable cause = super.getCause();
		if (cause == null) {
			return super.fillInStackTrace();
		} else {
			return cause.fillInStackTrace();
		}
	}

	/**
	 * 构造 HttpResponse
	 *
	 * @param request Request
	 * @param message message
	 * @return HttpResponse
	 */
	private static HttpResponse getResponse(Request request, String message) {
		Response response = new Response.Builder()
			.request(request)
			.protocol(Protocol.HTTP_1_1)
			.message(message)
			.code(500)
			.build();
		return new HttpResponse(response);
	}

}
