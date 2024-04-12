package io.ihankun.framework.cache.nats.core;

import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 默认的 nats 消息处理器
 *
 * @author hankun
 */
@Slf4j
public class DefaultMessageHandler implements MessageHandler {
	private final Object bean;
	private final Method method;

	public DefaultMessageHandler(Object bean, Method method) {
		this.bean = bean;
		this.method = makeAccessible(method);
	}

	private static Method makeAccessible(Method method) {
		ReflectionUtils.makeAccessible(method);
		return method;
	}

	@Override
	public void onMessage(Message msg) throws InterruptedException {
		try {
			ReflectionUtils.invokeMethod(method, bean, msg);
			msg.ack();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
}
