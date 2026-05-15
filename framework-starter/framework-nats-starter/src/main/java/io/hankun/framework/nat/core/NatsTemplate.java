package io.hankun.framework.nat.core;

import io.nats.client.Message;
import io.nats.client.Options;
import io.nats.client.impl.Headers;

/**
 * nats Template
 *
 * @author hankun
 */
public interface NatsTemplate {

    /**
     * Send a message to the specified subject.
     *
     * @param subject the subject to send the message to
     * @param body the message body
     */
    void publish(String subject, byte[] body);

    /**
     * Send a message to the specified subject with headers.
     *
     * @param subject the subject to send the message to
     * @param headers Optional headers to publish with the message.
     * @param body the message body
     */
    void publish(String subject, Headers headers, byte[] body);

    /**
     * Send a request to the specified subject, providing a replyTo subject.
     *
     * @param subject the subject to send the message to
     * @param replyTo the subject the receiver should send the response to
     * @param body the message body
     */
    void publish(String subject, String replyTo, byte[] body);

    /**
     * Send a request to the specified subject, providing a replyTo subject and headers.
     *
     * @param subject the subject to send the message to
     * @param replyTo the subject the receiver should send the response to
     * @param headers Optional headers to publish with the message.
     * @param body the message body
     */
    void publish(String subject, String replyTo, Headers headers, byte[] body);

    /**
     * Send a message to the specified subject.
     *
     * @param message the message
     */
    void publish(Message message);

}
