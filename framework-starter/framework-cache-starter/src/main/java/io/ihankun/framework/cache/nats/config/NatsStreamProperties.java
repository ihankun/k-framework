package io.ihankun.framework.cache.nats.config;

import io.nats.client.api.DeliverPolicy;
import io.nats.client.api.DiscardPolicy;
import io.nats.client.api.RetentionPolicy;
import io.nats.client.api.StorageType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;
import java.util.Map;

/**
 * nats stream 配置
 *
 * @author hankun
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(NatsStreamProperties.PREFIX)
public class NatsStreamProperties {
	public static final String PREFIX = NatsProperties.PREFIX + ".stream";

	/**
	 * 是否开启 nats JetStream，默认为：false
	 */
	private boolean enable = false;
	/**
	 * 名称，默认的 nats jetStream stream 名称
	 */
	private String name;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 消费者名称
	 */
	private String consumerName;
	/**
	 * 消费者分组
	 */
	private String consumerGroup;
	/**
	 * 消费策略
	 */
	private DeliverPolicy consumerPolicy= DeliverPolicy.Last;
	/**
	 * 保留策略，默认：限流
	 */
	private RetentionPolicy retentionPolicy = RetentionPolicy.Limits;
	/**
	 * 最大的消费者数，默认：不限制
	 */
	private long maxConsumers = -1;
	/**
	 * 最大的消息量，默认：不限制
	 */
	private long maxMsgs = -1;
	/**
	 * 每个订阅者最大的消息量，默认：不限制
	 */
	private long maxMsgsPerSubject = -1;
	/**
	 * 最大的字节数，默认：不限制
	 */
	private long maxBytes = -1;
	/**
	 * 消息有效期，默认：0
	 */
	private Duration maxAge = Duration.ZERO;
	/**
	 * 最大消息大小，默认：不限制
	 */
	private long maxMsgSize = -1;
	/**
	 * 存储类型，默认：文件
	 */
	private StorageType storageType = StorageType.File;
	/**
	 * 副本数，默认：1
	 */
	private int replicas = 1;
	/**
	 * 不进行 ack，默认：false
	 */
	private boolean noAck = false;
	/**
	 * 模板所有者
	 */
	private String templateOwner;
	/**
	 * 丢弃策略，默认：老的
	 */
	private DiscardPolicy discardPolicy = DiscardPolicy.Old;
	/**
	 * 复制窗口时间，默认：0
	 */
	private Duration duplicateWindow = Duration.ZERO;
	/**
	 * 是否已封存，默认：false
	 */
	private boolean sealed = false;
	/**
	 * 是否允许滚动合并，默认：false
	 */
	private boolean allowRollup = false;
	/**
	 * 是否允许直接传递，默认：false
	 */
	private boolean allowDirect = false;
	/**
	 * 是否启用镜像传递，默认：false
	 */
	private boolean mirrorDirect = false;
	/**
	 * 是否拒绝删除，默认：false
	 */
	private boolean denyDelete = false;
	/**
	 * 是否拒绝清除，默认：false
	 */
	private boolean denyPurge = false;
	/**
	 * 是否丢弃新的主题，默认：false
	 */
	private boolean discardNewPerSubject = false;
	/**
	 * 元数据
	 */
	private Map<String, String> metadata;

}
