package io.ihankun.framework.cache.pubsub;

import io.ihankun.framework.core.utils.plus.ReflectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Redisson 监听器
 *
 * @author hankun
 */
@Slf4j
@RequiredArgsConstructor
public class RPubSubListenerDetector implements BeanPostProcessor {
	private final RedisMessageListenerContainer redisMessageListenerContainer;
	private final RedisSerializer<Object> redisSerializer;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		ReflectionUtils.doWithMethods(userClass, method -> {
			RPubSubListener listener = AnnotationUtils.findAnnotation(method, RPubSubListener.class);
			if (listener != null) {
				String channel = listener.value();
				Assert.hasText(channel, "@RPubSubListener value channel must not be empty.");
				log.info("Found @RPubSubListener on bean:{} method:{}", beanName, method);

				// 校验 method，method 入参数大于等于1
				int paramCount = method.getParameterCount();
				if (paramCount > 1) {
					throw new IllegalArgumentException("@RPubSubListener on method " + method + " parameter count must less or equal to 1.");
				}
				// 精准模式和模糊匹配模式
				Topic topic = ChannelUtil.getTopic(channel);
				redisMessageListenerContainer.addMessageListener((message, pattern) -> {
					String messageChannel = new String(message.getChannel());
					Object body = redisSerializer.deserialize(message.getBody());
					invokeMethod(bean, method, paramCount, new RPubSubEvent<>(channel, messageChannel, body));
				}, topic);
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return bean;
	}

	private static void invokeMethod(Object bean, Method method, int paramCount, RPubSubEvent<Object> topicEvent) {
		// 支持没有参数的方法
		if (paramCount == 0) {
			ReflectUtil.invokeMethod(method, bean);
		} else {
			ReflectUtil.invokeMethod(method, bean, topicEvent);
		}
	}

}
