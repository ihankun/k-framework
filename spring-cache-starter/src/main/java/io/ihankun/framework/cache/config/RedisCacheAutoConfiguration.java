//package io.ihankun.framework.cache.config;
//
//import io.ihankun.framework.common.utils.string.CharPool;
//import io.ihankun.framework.common.utils.string.StringPool;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
//import org.springframework.boot.autoconfigure.cache.CacheProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheWriter;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.lang.Nullable;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * 扩展redis-cache支持注解cacheName添加超时时间
// * <p>
// *
// * @author hankun
// */
//@EnableCaching
////@AutoConfiguration(before = CacheAutoConfiguration.class)
//@EnableConfigurationProperties(CacheProperties.class)
//public class RedisCacheAutoConfiguration {
//
//	/**
//	 * 序列化方式
//	 */
//	private final RedisSerializer<Object> redisSerializer;
//	private final CacheProperties cacheProperties;
//	private final CacheManagerCustomizers customizerInvoker;
//	@Nullable
//	private final RedisCacheConfiguration redisCacheConfiguration;
//
//	RedisCacheAutoConfiguration(RedisSerializer<Object> redisSerializer,
//                                CacheProperties cacheProperties,
//                                CacheManagerCustomizers customizerInvoker,
//                                ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration) {
//		this.redisSerializer = redisSerializer;
//		this.cacheProperties = cacheProperties;
//		this.customizerInvoker = customizerInvoker;
//		this.redisCacheConfiguration = redisCacheConfiguration.getIfAvailable();
//	}
//
//	@Primary
//	@Bean("cacheResolver")
//	public CacheManager redisCacheManager(ObjectProvider<RedisConnectionFactory> connectionFactoryObjectProvider) {
//		RedisConnectionFactory connectionFactory = connectionFactoryObjectProvider.getIfAvailable();
//		Objects.requireNonNull(connectionFactory, "Bean RedisConnectionFactory is null.");
//		RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
//		RedisCacheConfiguration cacheConfiguration = this.determineConfiguration();
//		List<String> cacheNames = this.cacheProperties.getCacheNames();
//		Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
//		if (!cacheNames.isEmpty()) {
//			Map<String, RedisCacheConfiguration> cacheConfigMap = new LinkedHashMap<>(cacheNames.size());
//			cacheNames.forEach(it -> cacheConfigMap.put(it, cacheConfiguration));
//			initialCaches.putAll(cacheConfigMap);
//		}
//		boolean allowInFlightCacheCreation = true;
//		boolean enableTransactions = false;
//		RedisAutoCacheManager cacheManager = new RedisAutoCacheManager(
//			redisCacheWriter, cacheConfiguration, allowInFlightCacheCreation, initialCaches
//		);
//		cacheManager.setTransactionAware(enableTransactions);
//		return this.customizerInvoker.customize(cacheManager);
//	}
//
//	private RedisCacheConfiguration determineConfiguration() {
//		if (this.redisCacheConfiguration != null) {
//			return this.redisCacheConfiguration;
//		} else {
//			CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
//			RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//			// 设置默认缓存名分割符号为 “:”，如果已经带 “:” 则不设置。
//			config = config.computePrefixWith(name -> name.endsWith(StringPool.COLON) ? name : name + CharPool.COLON);
//			config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));
//			if (redisProperties.getTimeToLive() != null) {
//				config = config.entryTtl(redisProperties.getTimeToLive());
//			}
//            //prefixCacheNameWith
//			if (redisProperties.getKeyPrefix() != null) {
//				config = config.prefixKeysWith(redisProperties.getKeyPrefix());
//			}
//			if (!redisProperties.isCacheNullValues()) {
//				config = config.disableCachingNullValues();
//			}
//			if (!redisProperties.isUseKeyPrefix()) {
//				config = config.disableKeyPrefix();
//			}
//			return config;
//		}
//	}
//}
