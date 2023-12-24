package io.ihankun.framework.cache.nats.annotation;

import java.lang.annotation.*;

/**
 * nats 监听器
 *
 * @author hankun
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NatsListener {

	/**
	 * 主题 subject
	 *
	 * @return subject
	 */
	String value();

	/**
	 * 队列
	 * @return 队列名称
	 */
	String queue() default "";

}
