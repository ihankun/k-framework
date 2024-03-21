package io.ihankun.framework.captcha.v1.draw;

import java.awt.*;
import java.util.Random;

/**
 * 画验证码
 *
 * @author hankun
 */
public interface CaptchaDraw {

	/**
	 * 画验证码层
	 *
	 * @param g      Graphics2D
	 * @param width  画布宽度
	 * @param height 画布高度
	 * @param fonts  字体
	 * @param random Random
	 * @return 返回验证码 code
	 */
	String draw(Graphics2D g, int width, int height, Font[] fonts, Random random);

	/**
	 * 校验
	 *
	 * @param code             code
	 * @param userInputCaptcha 用户输入的字符串
	 * @return 是否成功
	 */
	boolean validate(String code, String userInputCaptcha);

}
