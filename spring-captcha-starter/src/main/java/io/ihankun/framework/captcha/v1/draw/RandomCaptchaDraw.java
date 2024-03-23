package io.ihankun.framework.captcha.v1.draw;

import io.ihankun.framework.captcha.v1.utils.CaptchaUtil;
import io.ihankun.framework.common.v1.utils.string.StringUtil;
import org.springframework.util.ObjectUtils;

import java.awt.*;
import java.util.Random;

/**
 * 随机字符串验证码
 *
 * @author hankun
 */
public class RandomCaptchaDraw implements CaptchaDraw {
	/**
	 * 默认的验证码数量，由于字体大小定死，后期再扩展自动一数量
	 */
	private static final int CODE_SIZE = 4;

	/**
	 * 验证码随机字符数组
	 */
	private static final char[] CHAR_ARRAY = "3456789ABCDEFGHJKMNPQRSTUVWXY".toCharArray();

	private final int codeSize;

	public RandomCaptchaDraw() {
		this(CODE_SIZE);
	}

	public RandomCaptchaDraw(int codeSize) {
		this.codeSize = codeSize;
	}

	@Override
	public String draw(Graphics2D g, int width, int height, Font[] fonts, Random random) {
		// 取随机产生的认证码(4位数字)
		String code = generateCode(random, codeSize);
		char[] buffer = code.toCharArray();
		for (int i = 0; i < buffer.length; i++) {
			//旋转度数 最好小于45度
			int degree = random.nextInt(25);
			if (i % 2 == 0) {
				degree = -degree;
			}
			//定义坐标
			int x = 27 * i;
			int y = 28;
			//旋转区域
			double radians = Math.toRadians(degree);
			g.rotate(radians, x, y);
			//设定字体颜色
			g.setColor(CaptchaUtil.randColor(random, 20, 130));
			//设定字体，每次随机
			Font fontTemp = fonts[random.nextInt(fonts.length)];
			Font font = fontTemp.deriveFont(Font.BOLD, (width - 10F) / codeSize * 1.2F);
			g.setFont(font);
			char xcode = buffer[i];
			//将认证码显示到图象中
			g.drawString(String.valueOf(xcode), x + 8, y + 10);
			//旋转之后，必须旋转回来
			g.rotate(-radians, x, y);
		}
		return code;
	}

	/**
	 * 生成验证码字符串
	 *
	 * @param random Random
	 * @return 验证码字符串
	 */
	private static String generateCode(Random random, int size) {
		char[] buffer = new char[size];
		for (int i = 0; i < size; i++) {
			buffer[i] = CHAR_ARRAY[random.nextInt(CHAR_ARRAY.length)];
		}
		return new String(buffer);
	}

	@Override
	public boolean validate(String code, String userInputCaptcha) {
		if (StringUtil.isBlank(userInputCaptcha)) {
			return false;
		}
		// 转成大写重要
		return ObjectUtils.nullSafeEquals(code, userInputCaptcha.toUpperCase());
	}

}
