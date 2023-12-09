package io.ihankun.framework.nats.core;

import io.ihankun.framework.common.utils.exception.Exceptions;
import io.ihankun.framework.common.utils.string.StringUtil;
import io.ihankun.framework.nats.annotation.NatsStreamListener;
import io.ihankun.framework.nats.config.NatsStreamCustomizer;
import io.ihankun.framework.nats.config.NatsStreamProperties;
import io.ihankun.framework.nats.utils.StreamConfigurationUtil;
import io.nats.client.*;
import io.nats.client.api.ConsumerConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * nats JetStream 监听器处理
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class NatsStreamListenerDetector implements BeanPostProcessor {
	private final NatsStreamProperties properties;
	private final ObjectProvider<NatsStreamCustomizer> natsStreamCustomizerObjectProvider;
	private final Connection natsConnection;
	private final JetStream jetStream;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		ReflectionUtils.doWithMethods(userClass, method -> {
			NatsStreamListener listener = AnnotationUtils.findAnnotation(method, NatsStreamListener.class);
			if (listener != null) {
				String subject = listener.value();
				Assert.hasText(subject, "@NatsStreamListener value(subject) must not be empty.");
				// 消息处理器
				MessageHandler messageHandler = new DefaultMessageHandler(bean, method);
				try {
					jetStreamSubscribe(listener, messageHandler);
				} catch (JetStreamApiException | IOException e) {
					throw Exceptions.unchecked(e);
				}
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return bean;
	}

	/**
	 * JetStream 订阅
	 *
	 * @param listener       NatsStreamListener
	 * @param messageHandler MessageHandler
	 */
	private void jetStreamSubscribe(NatsStreamListener listener, MessageHandler messageHandler)
		throws JetStreamApiException, IOException {
		String subject = listener.value();
		// 调度器
		Dispatcher dispatcher = natsConnection.createDispatcher(messageHandler);
		long pendingMessageLimit = listener.pendingMessageLimit();
		long pendingByteLimit = listener.pendingByteLimit();
		dispatcher.setPendingLimits(pendingMessageLimit, pendingByteLimit);
		// 订阅策略
		PushSubscribeOptions.Builder optionsBuilder = PushSubscribeOptions.builder()
			.pendingMessageLimit(pendingMessageLimit)
			.pendingByteLimit(pendingByteLimit)
			.ordered(listener.ordered());
		// stream 流名称
		String listenerStream = listener.stream();
		String streamName = properties.getName();
		// 判断监听器上的 Stream Name
		if (StringUtils.hasText(listenerStream) && !streamName.equals(listenerStream)) {
			optionsBuilder.stream(listenerStream);
			JetStreamManagement jsm = natsConnection.jetStreamManagement();
			// 不是默认的流，则添加流
			jsm.addStream(StreamConfigurationUtil.from(listenerStream, subject, properties, natsStreamCustomizerObjectProvider));
		}
		ConsumerConfiguration config = ConsumerConfiguration.builder()
			.durable(properties.getConsumerName() + '-' + StringUtil.getUUID())
			.deliverGroup(properties.getConsumerGroup())
			.deliverPolicy(properties.getConsumerPolicy())
			.build();
		optionsBuilder.configuration(config);
		// 队列
		String queue = listener.queue();
		// 是否自动 ack
		boolean autoAck = listener.autoAck();
		if (StringUtils.hasText(queue)) {
			jetStream.subscribe(subject, queue, dispatcher, messageHandler, autoAck, optionsBuilder.build());
		} else {
			jetStream.subscribe(subject, dispatcher, messageHandler, autoAck, optionsBuilder.build());
		}
	}


}
