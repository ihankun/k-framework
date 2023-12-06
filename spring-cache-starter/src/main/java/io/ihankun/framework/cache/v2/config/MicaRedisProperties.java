package io.ihankun.framework.cache.v2.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;

/**
 * redis 配置
 *
 * @author hankun
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(MicaRedisProperties.PREFIX)
public class MicaRedisProperties {
	public static final String PREFIX = "mica.redis";

	/**
	 * redis key 前缀
	 */
	private String keyPrefix;
	/**
	 * 序列化方式
	 */
	private SerializerType serializerType = SerializerType.JSON;
	/**
	 * key 过期事件
	 */
	private KeyExpiredEvent keyExpiredEvent = new KeyExpiredEvent();
	/**
	 * 限流配置
	 */
	private RateLimiter rateLimiter = new RateLimiter();
	/**
	 * stream
	 */
	private Stream stream = new Stream();

	/**
	 * 序列化方式
	 */
	public enum SerializerType {
		/**
		 * json 序列化
		 */
		JSON,
		/**
		 * jdk 序列化
		 */
		JDK
	}

	@Getter
	@Setter
	public static class KeyExpiredEvent {
		/**
		 * 是否开启 redis key 失效事件.
		 */
		boolean enable = false;
	}

	@Getter
	@Setter
	public static class RateLimiter {
		/**
		 * 是否开启 RateLimiter
		 */
		boolean enable = false;
	}

	@Getter
	@Setter
	public static class Stream {
		public static final String PREFIX = MicaRedisProperties.PREFIX + ".stream";
		/**
		 * 是否开启 stream
		 */
		boolean enable = false;
		/**
		 * consumer group，默认：服务名 + 环境
		 */
		String consumerGroup;
		/**
		 * 消费者名称，默认：ip + 端口
		 */
		String consumerName;
		/**
		 * poll 批量大小
		 */
		Integer pollBatchSize;
		/**
		 * poll 超时时间
		 */
		Duration pollTimeout;
	}

}
