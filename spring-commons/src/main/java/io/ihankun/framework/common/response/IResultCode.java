package io.ihankun.framework.common.response;

import java.io.Serializable;

/**
 * 状态码接口
 *
 * @author hankun
 */
public interface IResultCode extends Serializable {

	/**
	 * 返回的code码
	 *
	 * @return code
	 */
	int getCode();

	/**
	 * 返回的消息
	 *
	 * @return 消息
	 */
	default String getMsg() {
		return "系统未知异常";
	}
}
