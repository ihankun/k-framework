package io.ihankun.framework.captcha.v3.draw;

import java.awt.*;
import java.util.Random;

/**
 * 背景画
 *
 * @author hankun
 */
public interface BackgroundDraw {

	/**
	 * 画背景层
	 *
	 * @param g      Graphics2D
	 * @param width  画布宽度
	 * @param height 画布高度
	 * @param fonts  字体
	 * @param random Random
	 */
	void draw(Graphics2D g, int width, int height, Font[] fonts, Random random);

}
