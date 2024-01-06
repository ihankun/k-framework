package io.ihankun.framework.cache.pubsub;

import java.lang.annotation.*;

/**
 * 基于 Redisson 的消息监听器
 *
 * @author hankun
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RPubSubListener {

	/**
	 * topic name，支持通配符， 如 *、? 和 [...]
	 *
	 * @return String
	 */
	String value();

}
