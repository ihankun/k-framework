//package io.ihankun.framework.cache.config;
//
//import io.ihankun.framework.cache.RedisCache;
//import io.ihankun.framework.cache.pubsub.RPubSubListenerDetector;
//import io.ihankun.framework.cache.pubsub.RPubSubPublisher;
//import io.ihankun.framework.cache.pubsub.RedisPubSubPublisher;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//
///**
// * Redisson pub/sub 发布配置
// *
// * @author hankun
// */
//@Configuration
//public class RedisPubSubConfiguration {
//
//	@Bean
//	@ConditionalOnMissingBean
//	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
//		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//		container.setConnectionFactory(connectionFactory);
//		return container;
//	}
//
//	@Bean
//	public RPubSubPublisher topicEventPublisher(RedisCache redisCache,
//                                                RedisSerializer<Object> redisSerializer) {
//		return new RedisPubSubPublisher(redisCache, redisSerializer);
//	}
//
//	@Bean
//	public RPubSubListenerDetector topicListenerDetector(RedisMessageListenerContainer redisMessageListenerContainer,
//                                                         RedisSerializer<Object> redisSerializer) {
//		return new RPubSubListenerDetector(redisMessageListenerContainer, redisSerializer);
//	}
//
//}
