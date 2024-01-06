package io.ihankun.framework.cache.nats.utils;

import io.ihankun.framework.cache.nats.config.NatsStreamCustomizer;
import io.ihankun.framework.cache.nats.config.NatsStreamProperties;
import io.nats.client.api.StreamConfiguration;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * nats Stream 配置 工具
 *
 * @author hankun
 */
@UtilityClass
public class StreamConfigurationUtil {

	/**
	 * 构造 StreamConfiguration
	 *
	 * @param subject                            subject
	 * @param streamName                         stream name
	 * @param properties                         NatsStreamProperties
	 * @param natsStreamCustomizerObjectProvider ObjectProvider
	 * @return StreamConfiguration
	 */
	public static StreamConfiguration from(String subject,
										   String streamName,
										   NatsStreamProperties properties,
										   ObjectProvider<NatsStreamCustomizer> natsStreamCustomizerObjectProvider) {
		StreamConfiguration.Builder builder = StreamConfiguration.builder()
			.name(StringUtils.hasText(streamName) ? streamName : properties.getName())
			.description(properties.getDescription())
			.subjects(Collections.singletonList(subject))
			.retentionPolicy(properties.getRetentionPolicy())
			.maxConsumers(properties.getMaxConsumers())
			.maxMessages(properties.getMaxMsgs())
			.maxMessagesPerSubject(properties.getMaxMsgsPerSubject())
			.maxBytes(properties.getMaxBytes())
			.maxAge(properties.getMaxAge())
			.maxMsgSize(properties.getMaxMsgSize())
			.storageType(properties.getStorageType())
			.replicas(properties.getReplicas())
			.noAck(properties.isNoAck())
			.templateOwner(properties.getTemplateOwner())
			.discardPolicy(properties.getDiscardPolicy())
			.discardNewPerSubject(properties.isDiscardNewPerSubject())
			.duplicateWindow(properties.getDuplicateWindow())
			.allowRollup(properties.isAllowRollup())
			.allowDirect(properties.isAllowDirect())
			.denyDelete(properties.isDenyDelete())
			.denyPurge(properties.isDenyPurge())
			.metadata(properties.getMetadata());
		// 是否已封存
		if (properties.isSealed()) {
			builder.seal();
		}
		// 用户自定义配置
		natsStreamCustomizerObjectProvider.orderedStream().forEach(natsOptionsCustomizer -> natsOptionsCustomizer.customize(builder));
		return builder.build();
	}

}
