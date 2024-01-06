package io.ihankun.framework.captcha.config;

import io.ihankun.framework.captcha.cache.ICaptchaCache;
import io.ihankun.framework.captcha.cache.SpringCacheCaptchaCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码 spring cache 配置
 *
 * @author hankun
 */
@Configuration
public class SpringCacheCaptchaCacheConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ICaptchaCache captchaCache(CaptchaProperties properties,
									  CacheManager cacheManager) {
		return new SpringCacheCaptchaCache(properties, cacheManager);
	}

}
