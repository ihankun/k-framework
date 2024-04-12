package io.ihankun.framework.captcha.v1.config;

import io.ihankun.framework.captcha.v1.cache.ICaptchaCache;
import io.ihankun.framework.captcha.v1.entity.Captcha;
import io.ihankun.framework.captcha.v1.core.CaptchaServiceImpl;
import io.ihankun.framework.captcha.v1.ICaptchaService;
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
	prefix = CaptchaProperties.PREFIX,
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
//@ImportRuntimeHints(MicaCaptchaRuntimeHintsRegistrar.class)
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Captcha imageCaptcha(CaptchaProperties properties) {
		return new Captcha(properties.getCaptchaType());
	}

	@Bean
	@ConditionalOnMissingBean
	public ICaptchaService imageCaptchaService(CaptchaProperties properties,
											   ICaptchaCache captchaCache,
											   Captcha captcha) {
		return new CaptchaServiceImpl(properties, captchaCache, captcha);
	}

}
