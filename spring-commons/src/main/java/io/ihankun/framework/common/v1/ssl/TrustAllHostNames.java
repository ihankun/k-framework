package io.ihankun.framework.common.v1.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 信任所有 host name
 *
 * @author hankun
 */
public enum TrustAllHostNames implements HostnameVerifier {
	/**
	 * 实例
	 */
	INSTANCE;

	@Override
	public boolean verify(String s, SSLSession sslSession) {
		return true;
	}
}
