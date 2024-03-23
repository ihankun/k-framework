package io.ihankun.framework.captcha.v1.draw;


import io.ihankun.framework.captcha.v1.utils.CaptchaUtil;
import io.ihankun.framework.common.v1.utils.string.StringUtil;

import java.awt.*;
import java.util.Random;

/**
 * 算术型验证码
 *
 * @author hankun
 */
public class MathCaptchaDraw implements CaptchaDraw {

	@Override
	public String draw(Graphics2D g, int width, int height, Font[] fonts, Random random) {
		//设定字体，每次随机
		Font fontTemp = fonts[random.nextInt(fonts.length)];
		// 深色
		g.setColor(CaptchaUtil.randColor(random, 0, 100));
		// 数学公式
		String expr = Expression.randomExpr(random);
		// 补全验证码图案 1+1=?
		String text = expr + "=?";
		// 偏移半个字体大小
		int len = text.length();
		Font font = fontTemp.deriveFont(Font.BOLD, (width - 10F) / len * 1.6F);
		g.setFont(font);
		FontMetrics fontMetrics = g.getFontMetrics();
		// 计算宽度
		int textWidth = 0;
		for (int i = 0; i < len; i++) {
			char charAt = text.charAt(i);
			int charWidth = fontMetrics.charWidth(charAt);
			textWidth += charWidth;
		}
		// 保证居中
		float x = (width - textWidth) / 2.0F;
		// 高度
		float y = height / 4.0F * 3;
		g.drawString(text, x, y);
		return expr;
	}

	@Override
	public boolean validate(String code, String userInputCaptcha) {
		if (!StringUtil.isNumeric(userInputCaptcha)) {
			return false;
		}
		int captchaNum = Integer.parseInt(userInputCaptcha);
		int evalNum = Expression.eval(code);
		return captchaNum == evalNum;
	}

}
