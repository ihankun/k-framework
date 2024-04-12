package io.ihankun.framework.cache.nats.core;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import lombok.RequiredArgsConstructor;

/**
 * 默认的 NatsTemplate
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class DefaultNatsTemplate implements NatsTemplate {
	private final Connection connection;

	@Override
	public void publish(String subject, byte[] body) {
		connection.publish(subject, body);
	}

	@Override
	public void publish(String subject, Headers headers, byte[] body) {
		connection.publish(subject, headers, body);
	}

	@Override
	public void publish(String subject, String replyTo, byte[] body) {
		connection.publish(subject, replyTo, body);
	}

	@Override
	public void publish(String subject, String replyTo, Headers headers, byte[] body) {
		connection.publish(subject, replyTo, headers, body);
	}

	@Override
	public void publish(Message message) {
		connection.publish(message);
	}

}
