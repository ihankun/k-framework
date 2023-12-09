package io.ihankun.framework.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * cookie 管理
 *
 * @author hankun
 */
@ParametersAreNonnullByDefault
public class InMemoryCookieManager implements CookieJar {
	private final Set<Cookie> cookieSet;

	public InMemoryCookieManager() {
		this.cookieSet = new CopyOnWriteArraySet<>();
	}

	@Override
	public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
		cookieSet.addAll(cookies);
	}

	@Override
	public List<Cookie> loadForRequest(HttpUrl url) {
		List<Cookie> needRemoveCookieList = new ArrayList<>();
		List<Cookie> matchedCookieList = new ArrayList<>();
		for (Cookie cookie : cookieSet) {
			if (isCookieExpired(cookie)) {
				needRemoveCookieList.add(cookie);
			} else if (cookie.matches(url)) {
				matchedCookieList.add(cookie);
			}
		}
		// 清除过期 cookie
		if (!needRemoveCookieList.isEmpty()) {
			needRemoveCookieList.forEach(cookieSet::remove);
		}
		return matchedCookieList;
	}

	private static boolean isCookieExpired(Cookie cookie) {
		return cookie.expiresAt() < System.currentTimeMillis();
	}

}
