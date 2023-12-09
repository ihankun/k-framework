package io.ihankun.framework.nats.core;

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
     * Send a message to the specified subject. The message body <strong>will
     * not</strong> be copied. The expected usage with string content is something
     * like:
     *
     * <pre>
     * nc = Nats.connect()
     * nc.publish("destination", "message".getBytes("UTF-8"))
     * </pre>
     *
     * where the sender creates a byte array immediately before calling publish.
     * See {@link #publish(String, String, byte[]) publish()} for more details on
     * publish during reconnect.
     *
     * @param subject the subject to send the message to
     * @param body the message body
     * @throws IllegalStateException if the reconnect buffer is exceeded
     */
    void publish(String subject, byte[] body);

    /**
     * Send a message to the specified subject. The message body <strong>will
     * not</strong> be copied. The expected usage with string content is something
     * like:
     *
     * <pre>
     * nc = Nats.connect()
     * Headers h = new Headers().put("key", "value");
     * nc.publish("destination", h, "message".getBytes("UTF-8"))
     * </pre>
     *
     * where the sender creates a byte array immediately before calling publish.
     * See {@link #publish(String, String, byte[]) publish()} for more details on
     * publish during reconnect.
     *
     * @param subject the subject to send the message to
     * @param headers Optional headers to publish with the message.
     * @param body the message body
     * @throws IllegalStateException if the reconnect buffer is exceeded
     */
    void publish(String subject, Headers headers, byte[] body);

    /**
     * Send a request to the specified subject, providing a replyTo subject. The
     * message body <strong>will not</strong> be copied. The expected usage with
     * string content is something like:
     *
     * <pre>
     * nc = Nats.connect()
     * nc.publish("destination", "reply-to", "message".getBytes("UTF-8"))
     * </pre>
     *
     * where the sender creates a byte array immediately before calling publish.
     * <p>
     * During reconnect the client will try to buffer messages. The buffer size is set
     * in the connect options, see {@link Options.Builder#reconnectBufferSize(long) reconnectBufferSize()}
     * with a default value of {@link Options#DEFAULT_RECONNECT_BUF_SIZE 8 * 1024 * 1024} bytes.
     * If the buffer is exceeded an IllegalStateException is thrown. Applications should use
     * this exception as a signal to wait for reconnect before continuing.
     * </p>
     * @param subject the subject to send the message to
     * @param replyTo the subject the receiver should send the response to
     * @param body the message body
     * @throws IllegalStateException if the reconnect buffer is exceeded
     */
    void publish(String subject, String replyTo, byte[] body);

    /**
     * Send a request to the specified subject, providing a replyTo subject. The
     * message body <strong>will not</strong> be copied. The expected usage with
     * string content is something like:
     *
     * <pre>
     * nc = Nats.connect()
     * Headers h = new Headers().put("key", "value");
     * nc.publish("destination", "reply-to", h, "message".getBytes("UTF-8"))
     * </pre>
     *
     * where the sender creates a byte array immediately before calling publish.
     * <p>
     * During reconnect the client will try to buffer messages. The buffer size is set
     * in the connect options, see {@link Options.Builder#reconnectBufferSize(long) reconnectBufferSize()}
     * with a default value of {@link Options#DEFAULT_RECONNECT_BUF_SIZE 8 * 1024 * 1024} bytes.
     * If the buffer is exceeded an IllegalStateException is thrown. Applications should use
     * this exception as a signal to wait for reconnect before continuing.
     * </p>
     * @param subject the subject to send the message to
     * @param replyTo the subject the receiver should send the response to
     * @param headers Optional headers to publish with the message.
     * @param body the message body
     * @throws IllegalStateException if the reconnect buffer is exceeded
     */
    void publish(String subject, String replyTo, Headers headers, byte[] body);

    /**
     * Send a message to the specified subject. The message body <strong>will
     * not</strong> be copied. The expected usage with string content is something
     * like:
     *
     * <pre>
     * nc = Nats.connect()
     * nc.publish(NatsMessage.builder()...build())
     * </pre>
     *
     * where the sender creates a byte array immediately before calling publish.
     * See {@link #publish(String, String, byte[]) publish()} for more details on
     * publish during reconnect.
     *
     * @param message the message
     * @throws IllegalStateException if the reconnect buffer is exceeded
     */
    void publish(Message message);

}
