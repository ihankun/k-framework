package io.ihankun.framework.core.ssl;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 不进行证书校验
 *
 * @author hankun
 */
public enum DisableValidationTrustManager implements X509TrustManager {

	/**
	 * 实例
	 */
	INSTANCE;

	/**
	 * 获取 TrustManagers
	 *
	 * @return TrustManager 数组
	 */
	public TrustManager[] getTrustManagers() {
		return new TrustManager[]{this};
	}

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

}
