package io.ihankun.framework.nats.core;

import io.ihankun.framework.common.utils.exception.Exceptions;
import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.PublishOptions;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

/**
 * nats JetStream 封装
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class DefaultNatsStreamTemplate implements NatsStreamTemplate {
	private final JetStream jetStream;


	@Override
	public PublishAck publish(String subject, byte[] body) {
		try {
			return jetStream.publish(subject, body);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Override
	public PublishAck publish(String subject, Headers headers, byte[] body) {
		try {
			return jetStream.publish(subject, headers, body);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Override
	public PublishAck publish(String subject, byte[] body, PublishOptions options) {
		try {
			return jetStream.publish(subject, body, options);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Override
	public PublishAck publish(String subject, Headers headers, byte[] body, PublishOptions options) {
		try {
			return jetStream.publish(subject, headers, body, options);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Override
	public PublishAck publish(Message message) {
		try {
			return jetStream.publish(message);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Override
	public PublishAck publish(Message message, PublishOptions options) {
		try {
			return jetStream.publish(message, options);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}

	@Override
	public CompletableFuture<PublishAck> publishAsync(String subject, byte[] body) {
		return jetStream.publishAsync(subject, body);
	}

	@Override
	public CompletableFuture<PublishAck> publishAsync(String subject, Headers headers, byte[] body) {
		return jetStream.publishAsync(subject, headers, body);
	}

	@Override
	public CompletableFuture<PublishAck> publishAsync(String subject, byte[] body, PublishOptions options) {
		return jetStream.publishAsync(subject, body, options);
	}

	@Override
	public CompletableFuture<PublishAck> publishAsync(String subject, Headers headers, byte[] body, PublishOptions options) {
		return jetStream.publishAsync(subject, headers, body, options);
	}

	@Override
	public CompletableFuture<PublishAck> publishAsync(Message message) {
		return jetStream.publishAsync(message);
	}

	@Override
	public CompletableFuture<PublishAck> publishAsync(Message message, PublishOptions options) {
		return jetStream.publishAsync(message, options);
	}

}
