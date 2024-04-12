package io.ihankun.framework.captcha.v1.core;

import io.ihankun.framework.captcha.v1.ICaptchaService;
import io.ihankun.framework.captcha.v1.cache.ICaptchaCache;
import io.ihankun.framework.captcha.v1.config.CaptchaProperties;
import io.ihankun.framework.captcha.v1.entity.Captcha;
import io.ihankun.framework.core.utils.string.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;

/**
 * 验证码服务
 *
 * @author hankun
 */
@Slf4j
@RequiredArgsConstructor
public class CaptchaServiceImpl implements ICaptchaService {
	private final CaptchaProperties properties;
	private final ICaptchaCache captchaCache;
	private final Captcha captcha;

	@Override
	public void generate(String uuid, OutputStream outputStream) {
		String generate = captcha.generate(() -> outputStream);
		captchaCache.put(properties.getCacheName(), uuid, generate);
	}

	@Override
	public boolean validate(String uuid, String userInputCaptcha) {
		log.debug("validate captcha uuid is {}, userInputCaptcha is {}", uuid, userInputCaptcha);
		String code = captchaCache.getAndRemove(properties.getCacheName(), uuid);
		if (StringUtil.isBlank(code)) {
			return false;
		}
		return captcha.validate(code, userInputCaptcha);
	}

}
