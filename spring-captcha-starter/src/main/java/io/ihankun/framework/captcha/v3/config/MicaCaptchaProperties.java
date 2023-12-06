package io.ihankun.framework.captcha.v3.config;

import io.ihankun.framework.captcha.v3.enums.CaptchaType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 验证码配置
 *
 * @author hankun
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(MicaCaptchaProperties.PREFIX)
public class MicaCaptchaProperties {
	public static final String PREFIX = "mica.captcha";

	/**
	 * 验证码类型，默认：随机数
	 */
	private CaptchaType captchaType = CaptchaType.RANDOM;
	/**
	 * 验证码cache名，默认：captcha:cache#5m，配合 mica-redis 5分钟缓存
	 */
	private String cacheName = "captcha:cache#5m";

}
