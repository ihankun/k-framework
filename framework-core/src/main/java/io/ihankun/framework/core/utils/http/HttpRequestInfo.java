package io.ihankun.framework.core.utils.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * http 请求信息
 *
 * @author hankun
 */
@Getter
@Setter
@ToString
public class HttpRequestInfo implements Serializable {
	/**
	 * 请求方法
	 */
	private String method;
	/**
	 * 请求 url
	 */
	private String url;
	/**
	 * 请求 headers
	 */
	private Map<String, String> headers = new HashMap<>();
	/**
	 * 请求 body
	 */
	private String body;

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

}
