///*
// * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
// * <p>
// * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.gnu.org/licenses/lgpl.html
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package io.ihankun.framework.cache.v2.config;
//
//import io.ihankun.framework.cache.v2.stream.DefaultRStreamTemplate;
//import io.ihankun.framework.cache.v2.stream.RStreamListenerDetector;
//import io.ihankun.framework.cache.v2.stream.RStreamTemplate;
//import io.ihankun.framework.common.constants.base.Constants;
//import io.ihankun.framework.common.utils.CharPool;
//import io.ihankun.framework.common.utils.INetUtil;
//import io.ihankun.framework.common.utils.string.StringUtil;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.autoconfigure.web.ServerProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.stream.MapRecord;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.stream.StreamMessageListenerContainer;
//import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
//import org.springframework.util.ErrorHandler;
//
//import java.time.Duration;
//
///**
// * redis Stream 配置
// *
// * @author L.cm
// */
//@Configuration
//@ConditionalOnProperty(
//	prefix = MicaRedisProperties.Stream.PREFIX,
//	name = "enable",
//	havingValue = "true"
//)
//public class RedisStreamConfiguration {
//
//	@Bean
//	@ConditionalOnMissingBean
//	public StreamMessageListenerContainerOptions<String, MapRecord<String, String, byte[]>> streamMessageListenerContainerOptions(MicaRedisProperties properties,
//																																  ObjectProvider<ErrorHandler> errorHandlerObjectProvider) {
//		StreamMessageListenerContainer.StreamMessageListenerContainerOptionsBuilder<String, MapRecord<String, String, byte[]>> builder = StreamMessageListenerContainerOptions
//			.builder()
//			.keySerializer(RedisSerializer.string())
//			.hashKeySerializer(RedisSerializer.string())
//			.hashValueSerializer(RedisSerializer.byteArray());
//		MicaRedisProperties.Stream streamProperties = properties.getStream();
//		// 批量大小
//		Integer pollBatchSize = streamProperties.getPollBatchSize();
//		if (pollBatchSize != null && pollBatchSize > 0) {
//			builder.batchSize(pollBatchSize);
//		}
//		// poll 超时时间
//		Duration pollTimeout = streamProperties.getPollTimeout();
//		if (pollTimeout != null && !pollTimeout.isNegative()) {
//			builder.pollTimeout(pollTimeout);
//		}
//		// errorHandler
//		errorHandlerObjectProvider.ifAvailable((builder::errorHandler));
//		// TODO L.cm executor
//		return builder.build();
//	}
//
//	@Bean
//	@ConditionalOnMissingBean
//	public StreamMessageListenerContainer<String, MapRecord<String, String, byte[]>> streamMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
//																													StreamMessageListenerContainerOptions<String, MapRecord<String, String, byte[]>> streamMessageListenerContainerOptions) {
//		// 根据配置对象创建监听容器
//		return StreamMessageListenerContainer.create(redisConnectionFactory, streamMessageListenerContainerOptions);
//	}
//
//	@Bean
//	@ConditionalOnMissingBean
//	public RStreamListenerDetector streamListenerDetector(StreamMessageListenerContainer<String, MapRecord<String, String, byte[]>> streamMessageListenerContainer,
//														  RedisTemplate<String, Object> redisTemplate,
//														  ObjectProvider<ServerProperties> serverPropertiesObjectProvider,
//														  MicaRedisProperties properties,
//														  Environment environment) {
//		MicaRedisProperties.Stream streamProperties = properties.getStream();
//		// 消费组名称
//		String consumerGroup = streamProperties.getConsumerGroup();
//		if (StringUtil.isBlank(consumerGroup)) {
//			String appName = environment.getRequiredProperty(Constants.SPRING_APP_NAME_KEY);
//			String profile = environment.getProperty(Constants.ACTIVE_PROFILES_PROPERTY);
//			consumerGroup = StringUtil.isBlank(profile) ? appName : appName + CharPool.COLON + profile;
//		}
//		// 消费者名称
//		String consumerName = streamProperties.getConsumerName();
//		if (StringUtil.isBlank(consumerName)) {
//			final StringBuilder consumerNameBuilder = new StringBuilder(INetUtil.getHostIp());
//			serverPropertiesObjectProvider.ifAvailable(serverProperties -> {
//				consumerNameBuilder.append(CharPool.COLON).append(serverProperties.getPort());
//			});
//			consumerName = consumerNameBuilder.toString();
//		}
//		return new RStreamListenerDetector(streamMessageListenerContainer, redisTemplate, consumerGroup, consumerName);
//	}
//
//	@Bean
//	public RStreamTemplate streamTemplate(RedisTemplate<String, Object> redisTemplate) {
//		return new DefaultRStreamTemplate(redisTemplate);
//	}
//
//}
