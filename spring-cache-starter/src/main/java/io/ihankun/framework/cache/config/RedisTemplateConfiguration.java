//package io.ihankun.framework.cache.config;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
//import io.ihankun.framework.cache.cache.RedisCache;
//import io.ihankun.framework.cache.resolver.DefaultRedisKeyResolver;
//import io.ihankun.framework.cache.resolver.RedisKeyResolver;
//import io.ihankun.framework.cache.resolver.RedisKeyResolverSerializer;
//import io.ihankun.framework.common.utils.json.JsonUtil;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//
///**
// * RedisTemplate  配置
// *
// * @author hankun
// */
////@AutoConfiguration(after = RedisAutoConfiguration.class)
//@EnableConfigurationProperties(RedisConfigProperties.class)
//public class RedisTemplateConfiguration {
//
//	/**
//	 * 默认的 redis key 处理
//	 * @param properties MicaRedisProperties
//	 * @return RedisKeyResolver
//	 */
//	@Bean
//	@ConditionalOnMissingBean(RedisKeyResolver.class)
//	public RedisKeyResolver redisKeyResolver(RedisConfigProperties properties) {
//		return new DefaultRedisKeyResolver(properties);
//	}
//
//	/**
//	 * value 值 序列化
//	 *
//	 * @return RedisSerializer
//	 */
//	@Bean
//	@ConditionalOnMissingBean(RedisSerializer.class)
//	public RedisSerializer<Object> redisSerializer(RedisConfigProperties properties) {
//		RedisConfigProperties.SerializerType serializerType = properties.getSerializerType();
//		if (RedisConfigProperties.SerializerType.JDK == serializerType) {
//			ClassLoader classLoader = this.getClass().getClassLoader();
//			return new JdkSerializationRedisSerializer(classLoader);
//		}
//		// jackson findAndRegisterModules，use copy
//		ObjectMapper objectMapper = JsonUtil.getInstance().copy();
//		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//		// findAndRegisterModules
//		objectMapper.findAndRegisterModules();
//		// class type info to json
//		GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);
//		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL, As.PROPERTY);
//		return new GenericJackson2JsonRedisSerializer(objectMapper);
//	}
//
//	@Bean
//	@ConditionalOnMissingBean(name = "micaRedisTemplate")
//	@ConditionalOnSingleCandidate(RedisConnectionFactory.class)
//	public RedisTemplate<String, Object> micaRedisTemplate(RedisConnectionFactory redisConnectionFactory,
//														   RedisKeyResolver redisKeyResolver,
//														   RedisSerializer<Object> redisSerializer) {
//		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//		// key 序列化
//		RedisKeyResolverSerializer keySerializer = new RedisKeyResolverSerializer(RedisSerializer.string(), redisKeyResolver);
//		redisTemplate.setKeySerializer(keySerializer);
//		redisTemplate.setHashKeySerializer(keySerializer);
//		// value 序列化
//		redisTemplate.setValueSerializer(redisSerializer);
//		redisTemplate.setHashValueSerializer(redisSerializer);
//		redisTemplate.setConnectionFactory(redisConnectionFactory);
//		return redisTemplate;
//	}
//
//	@Bean
//	@ConditionalOnMissingBean(ValueOperations.class)
//	public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> micaRedisTemplate) {
//		return micaRedisTemplate.opsForValue();
//	}
//
//	@Bean
//	public RedisCache micaRedisCache(RedisTemplate<String, Object> micaRedisTemplate) {
//		return new RedisCache(micaRedisTemplate);
//	}
//}
