package io.ihankun.framework.captcha.v1.entity;

import java.io.OutputStream;
import java.util.function.Supplier;

/**
 * 验证码抽象
 *
 * @author hankun
 */
public interface ICaptcha {

	/**
	 * 生成验证码
	 *
	 * @param supplier Supplier
	 * @return 验证码文字
	 */
	String generate(Supplier<OutputStream> supplier);

	/**
	 * 校验验证码
	 *
	 * @param code             验证码
	 * @param userInputCaptcha 用户输入的验证码
	 * @return 是否成功
	 */
	boolean validate(String code, String userInputCaptcha);

}
