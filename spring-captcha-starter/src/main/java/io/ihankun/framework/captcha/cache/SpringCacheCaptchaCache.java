package io.ihankun.framework.captcha.cache;

import io.ihankun.framework.captcha.config.CaptchaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Objects;

/**
 * spring cache 的 captcha cache
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class SpringCacheCaptchaCache implements ICaptchaCache, InitializingBean {

	private final CaptchaProperties properties;

	private final CacheManager cacheManager;

	@Override
	public void put(String cacheName, String uuid, String value) {
		Cache captchaCache = getCache(cacheName);
		captchaCache.put(uuid, value);
	}

	@Override
	public String getAndRemove(String cacheName, String uuid) {
		Cache captchaCache = getCache(cacheName);
		String value = captchaCache.get(uuid, String.class);
		if (value != null) {
			captchaCache.evict(uuid);
		}
		return value;
	}

	/**
	 * 发现 caffeine 中会刷新会导致引用为 null
	 *
	 * @return Cache
	 */
	private Cache getCache(String cacheName) {
		return cacheManager.getCache(cacheName);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String cacheName = properties.getCacheName();
		Cache cache = cacheManager.getCache(cacheName);
		Objects.requireNonNull(cache, () -> "mica-captcha spring cache name " + cacheName + " is null.");
	}

}
