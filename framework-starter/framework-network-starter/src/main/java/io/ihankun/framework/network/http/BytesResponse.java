package io.ihankun.framework.network.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.ihankun.framework.core.utils.exception.Exceptions;
import io.ihankun.framework.core.utils.json.JsonUtil;
import okhttp3.*;
import okhttp3.internal.Util;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * body 使用 bytes 避免流关闭的问题，同时为了更好的支持异步
 *
 * @author hankun
 */
public class BytesResponse implements ResponseSpec, Closeable {
	private final Request request;
	private final Response response;
	private final ResponseBody responseBody;
	private final byte[] body;

	public BytesResponse(Response response) {
		this.request = response.request();
		this.response = response;
		this.responseBody = HttpResponse.ifNullBodyToEmpty(response.body());
		this.body = ifNullBodyToEmpty(response.body());
	}

	@Override
	public int code() {
		return response.code();
	}

	@Override
	public boolean isOk() {
		return response.isSuccessful();
	}

	@Override
	public String message() {
		return response.message();
	}

	@Override
	public boolean isRedirect() {
		return response.isRedirect();
	}

	@Override
	public Headers headers() {
		return response.headers();
	}

	@Override
	public List<Cookie> cookies() {
		return Cookie.parseAll(request.url(), this.headers());
	}

	@Override
	public String asString() {
		return asString(StandardCharsets.UTF_8);
	}

	@Override
	public String asString(Charset charset) {
		return new String(body, charset);
	}

	@Override
	public byte[] asBytes() {
		return body;
	}

	@Override
	public InputStream asStream() {
		return new ByteArrayInputStream(body);
	}

	@Override
	public JsonNode asJsonNode() {
		return JsonUtil.readTree(body);
	}

	@Nullable
	@Override
	public <T> T asValue(Class<T> valueType) {
		return JsonUtil.readValue(body, valueType);
	}

	@Nullable
	@Override
	public <T> T asValue(TypeReference<T> typeReference) {
		return JsonUtil.readValue(body, typeReference);
	}

	@Override
	public <T> List<T> asList(Class<T> valueType) {
		return JsonUtil.readList(body, valueType);
	}

	@Override
	public <K, V> Map<K, V> asMap(Class<?> keyClass, Class<?> valueType) {
		return JsonUtil.readMap(body, keyClass, valueType);
	}

	@Override
	public <V> Map<String, V> asMap(Class<?> valueType) {
		return JsonUtil.readMap(body, String.class, valueType);
	}

	@Override
	public File toFile(File file) {
		toFile(file.toPath());
		return file;
	}

	@Override
	public Path toFile(Path path) {
		try {
			return Files.write(path, body);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Nullable
	@Override
	public MediaType contentType() {
		return responseBody.contentType();
	}

	@Override
	public long contentLength() {
		return responseBody.contentLength();
	}

	@Override
	public Request rawRequest() {
		return request;
	}

	@Override
	public Response rawResponse() {
		return response;
	}

	@Nullable
	@Override
	public ResponseBody rawBody() {
		return responseBody;
	}

	@Override
	public String toString() {
		return response.toString();
	}

	static byte[] ifNullBodyToEmpty(@Nullable ResponseBody body) {
		if (body == null) {
			return Util.EMPTY_BYTE_ARRAY;
		}
		try {
			return body.bytes();
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Override
	public void close() throws IOException {
		Util.closeQuietly(response);
	}

}
