package io.ihankun.framework.common.http.v2;

import lombok.RequiredArgsConstructor;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * BaseAuth
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class BaseAuthenticator implements Authenticator {
	private final String userName;
	private final String password;

	@Override
	public Request authenticate(Route route, Response response) throws IOException {
		String credential = Credentials.basic(userName, password, StandardCharsets.UTF_8);
		return response.request().newBuilder()
			.header("Authorization", credential)
			.build();
	}
}
