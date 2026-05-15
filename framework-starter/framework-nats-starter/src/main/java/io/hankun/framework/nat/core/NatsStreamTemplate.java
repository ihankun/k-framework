package io.hankun.framework.nat.core;

import io.nats.client.Message;
import io.nats.client.PublishOptions;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;

import java.util.concurrent.CompletableFuture;

/**
 * nats stream Template
 *
 * @author hankun
 */
public interface NatsStreamTemplate {

    PublishAck publish(String subject, byte[] body);

    PublishAck publish(String subject, Headers headers, byte[] body);

    PublishAck publish(String subject, byte[] body, PublishOptions options);

    PublishAck publish(String subject, Headers headers, byte[] body, PublishOptions options);

    PublishAck publish(Message message);

    PublishAck publish(Message message, PublishOptions options);

    CompletableFuture<PublishAck> publishAsync(String subject, byte[] body);

    CompletableFuture<PublishAck> publishAsync(String subject, Headers headers, byte[] body);

    CompletableFuture<PublishAck> publishAsync(String subject, byte[] body, PublishOptions options);

    CompletableFuture<PublishAck> publishAsync(String subject, Headers headers, byte[] body, PublishOptions options);

    CompletableFuture<PublishAck> publishAsync(Message message);

    CompletableFuture<PublishAck> publishAsync(Message message, PublishOptions options);

}
