package io.ihankun.framework.captcha.v1.enums;

import io.ihankun.framework.captcha.v1.draw.CaptchaDraw;
import io.ihankun.framework.captcha.v1.draw.MathCaptchaDraw;
import io.ihankun.framework.captcha.v1.draw.RandomCaptchaDraw;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 验证码类型
 *
 * @author hankun
 */
@Getter
@RequiredArgsConstructor
public enum CaptchaType {

	/**
	 * 随机数
	 */
	RANDOM(new RandomCaptchaDraw()),
	/**
	 * 算术
	 */
	MATH(new MathCaptchaDraw());

	private final CaptchaDraw captchaDraw;
}
