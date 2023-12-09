package io.ihankun.framework.captcha.enums;

import io.ihankun.framework.captcha.draw.CaptchaDraw;
import io.ihankun.framework.captcha.draw.MathCaptchaDraw;
import io.ihankun.framework.captcha.draw.RandomCaptchaDraw;
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
