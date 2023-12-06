package io.ihankun.framework.common.utils.plus;

import io.ihankun.framework.common.utils.Charsets;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

/**
 * url处理工具类
 *
 * @author hankun
 */
public class UrlUtil extends UriUtils {

	/**
	 * encode
	 *
	 * @param source source
	 * @return sourced String
	 */
	public static String encode(String source) {
		return UriUtils.encode(source, Charsets.UTF_8);
	}

	/**
	 * decode
	 *
	 * @param source source
	 * @return decoded String
	 */
	public static String decode(String source) {
		return StringUtils.uriDecode(source, Charsets.UTF_8);
	}
}
