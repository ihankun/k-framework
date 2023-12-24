package io.ihankun.framework.cache.nats.core;

import io.ihankun.framework.cache.nats.annotation.NatsListener;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * nats 监听器处理
 *
 * @author hankun
 */
@RequiredArgsConstructor
public class NatsListenerDetector implements BeanPostProcessor {
    private final Connection natsConnection;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> userClass = ClassUtils.getUserClass(bean);
        ReflectionUtils.doWithMethods(userClass, method -> {
            NatsListener listener = AnnotationUtils.findAnnotation(method, NatsListener.class);
            if (listener != null) {
				// 订阅的主题
                String subject = listener.value();
                Assert.hasText(subject, "@NatsListener value(subject) must not be empty.");
				Dispatcher connectionDispatcher = natsConnection.createDispatcher();
				// 消息监听器
				MessageHandler messageHandler = new DefaultMessageHandler(bean, method);
                String queue = listener.queue();
                if (StringUtils.hasText(queue)) {
                    connectionDispatcher.subscribe(subject, queue, messageHandler);
                } else {
                    connectionDispatcher.subscribe(subject, messageHandler);
                }
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
        return bean;
    }

}
