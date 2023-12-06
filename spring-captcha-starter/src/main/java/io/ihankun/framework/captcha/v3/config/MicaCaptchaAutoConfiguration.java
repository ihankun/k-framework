package io.ihankun.framework.captcha.v3.config;

import io.ihankun.framework.captcha.v3.cache.ICaptchaCache;
import io.ihankun.framework.captcha.v3.core.Captcha;
import io.ihankun.framework.captcha.v3.service.CaptchaServiceImpl;
import io.ihankun.framework.captcha.v3.service.ICaptchaService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * 验证码自动配置
 *
 * @author hankun
 */
@Configuration
@ConditionalOnProperty(
	prefix = MicaCaptchaProperties.PREFIX,
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
//@ImportRuntimeHints(MicaCaptchaRuntimeHintsRegistrar.class)
@EnableConfigurationProperties(MicaCaptchaProperties.class)
public class MicaCaptchaAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Captcha imageCaptcha(MicaCaptchaProperties properties) {
		return new Captcha(properties.getCaptchaType());
	}

	@Bean
	@ConditionalOnMissingBean
	public ICaptchaService imageCaptchaService(MicaCaptchaProperties properties,
											   ICaptchaCache captchaCache,
											   Captcha captcha) {
		return new CaptchaServiceImpl(properties, captchaCache, captcha);
	}

}
