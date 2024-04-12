package io.ihankun.framework.core.utils.http;


import io.ihankun.framework.core.utils.exception.Exceptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;


/**
 * http 请求解析
 *
 * @author hankun
 */
public class HttpRequestParser {

	/**
	 * 解析 http 纯文本
	 *
	 * @param httpText httpText
	 * @return HttpRequest
	 */
	public static HttpRequestInfo parser(String httpText) {
		try (StringReader stringReader = new StringReader(httpText);
			 BufferedReader reader = new BufferedReader(stringReader)) {
			return httpParser(reader);
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	private static HttpRequestInfo httpParser(BufferedReader reader) throws IOException {
		// RequestLine
		String line = reader.readLine();
		if (line == null) {
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(line, " ");
		int countTokens = tokenizer.countTokens();
		if (countTokens < 2) {
			return null;
		}
		HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
		// method and url
		String method = tokenizer.nextToken();
		String url = tokenizer.nextToken();
		httpRequestInfo.setMethod(method.trim());
		httpRequestInfo.setUrl(url.trim());
		// 解析 header
		for (; ; ) {
			line = reader.readLine();
			if (line != null && line.isEmpty()) {
				break;
			}
			// 已经结束，没有 body
			if (line == null) {
				return httpRequestInfo;
			}
			// 解析 header 行，name: value
			tokenizer = new StringTokenizer(line, ":");
			if (tokenizer.countTokens() > 1) {
				String name = tokenizer.nextToken();
				String value = tokenizer.nextToken();
				httpRequestInfo.addHeader(name.trim(), value.trim());
			}
		}
		// 解析 body
		StringBuilder bodyBuilder = new StringBuilder();
		for (; ; ) {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			bodyBuilder.append(line);
		}
		// 处理 body
		String body = bodyBuilder.toString();
		if (!body.isEmpty()) {
			httpRequestInfo.setBody(body.trim());
		}
		return httpRequestInfo;
	}

}
