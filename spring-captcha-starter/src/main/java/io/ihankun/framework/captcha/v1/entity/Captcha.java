package io.ihankun.framework.captcha.v1.entity;

import io.ihankun.framework.captcha.v1.draw.*;
import io.ihankun.framework.captcha.v1.enums.CaptchaType;
import io.ihankun.framework.common.v1.utils.Holder;
import io.ihankun.framework.common.v1.utils.exception.Exceptions;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

/**
 * 验证码
 *
 * @author hankun
 */
public class Captcha implements ICaptcha {
	/**
	 * 默认的验证码大小，暂时不支持外部设置，因为字体大小是写死的，后期会加自动计算
	 */
	private static final int WIDTH = 130;

	private static final int HEIGHT = 48;

	private static final String[] FONT_NAMES = new String[]{"001.ttf", "002.ttf", "003.ttf", "004.ttf"};

	private BackgroundDraw backgroundDraw;
	private CaptchaDraw captchaDraw;

	private InterferenceDraw interferenceDraw;

	private Random random;

	private final Font[] fonts;

	public Captcha() {
		this(new RandomCaptchaDraw());
	}

	public Captcha(CaptchaType type) {
		this(type.getCaptchaDraw());
	}

	public Captcha(CaptchaDraw captchaDraw) {
		this(SmallCharsBackgroundDraw.INSTANCE, captchaDraw, CurveInterferenceDraw.INSTANCE, Holder.SECURE_RANDOM);
	}

	public Captcha(BackgroundDraw backgroundDraw,
				   CaptchaDraw captchaDraw,
				   InterferenceDraw interferenceDraw,
				   Random random) {
		this.backgroundDraw = backgroundDraw;
		this.captchaDraw = captchaDraw;
		this.interferenceDraw = interferenceDraw;
		this.random = random;
		this.fonts = loadFonts();
	}

	public void setBackgroundDraw(BackgroundDraw backgroundDraw) {
		this.backgroundDraw = Objects.requireNonNull(backgroundDraw, "BackgroundDraw is null.");
	}

	public void setCaptchaDraw(CaptchaDraw captchaDraw) {
		this.captchaDraw = Objects.requireNonNull(captchaDraw, "CaptchaDraw is null.");
	}

	public void setInterferenceDraw(InterferenceDraw interferenceDraw) {
		this.interferenceDraw = Objects.requireNonNull(interferenceDraw, "InterferenceDraw is null.");
	}

	public void setRandom(Random random) {
		this.random = Objects.requireNonNull(random, "Random is null.");
	}

	@Override
	public String generate(Supplier<OutputStream> supplier) {
		// 初始化画布
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = initGraphics(image);
		// 背景层
		backgroundDraw.draw(graphics, WIDTH, HEIGHT, fonts, random);
		// 验证码层
		String captcha = captchaDraw.draw(graphics, WIDTH, HEIGHT, fonts, random);
		// 干扰层
		interferenceDraw.draw(graphics, WIDTH, HEIGHT, fonts, random);
		try (OutputStream os = supplier.get()) {
			ImageIO.write(image, "JPEG", os);
			return captcha;
		} catch (IOException e) {
			throw Exceptions.unchecked(e);
		} finally {
			graphics.dispose();
		}
	}

	@Override
	public boolean validate(String code, String userInputCaptcha) {
		return captchaDraw.validate(code, userInputCaptcha);
	}

	private static Graphics2D initGraphics(BufferedImage image) {
		// 获取图形上下文
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		// 图形抗锯齿
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// 字体抗锯齿
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return graphics;
	}

	private static Font[] loadFonts() {
		List<Font> fontList = new ArrayList<>();
		for (String fontName : FONT_NAMES) {
			String path = "fonts/" + fontName;
			fontList.add(loadFont(new ClassPathResource(path)));
		}
		return fontList.toArray(new Font[0]);
	}

	@SneakyThrows
	private static Font loadFont(ClassPathResource resource) {
		return Font.createFont(Font.TRUETYPE_FONT, resource.getInputStream());
	}

}
