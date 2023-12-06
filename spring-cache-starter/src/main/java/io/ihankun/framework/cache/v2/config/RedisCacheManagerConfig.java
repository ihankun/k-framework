//package io.ihankun.framework.cache.v2.config;
//
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
//import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * CacheManagerCustomizers 配置
// *
// * @author hankun
// */
//@Configuration
//@ConditionalOnMissingBean(CacheManagerCustomizers.class)
//public class RedisCacheManagerConfig {
//
//	@Bean
//	public CacheManagerCustomizers cacheManagerCustomizers(
//		ObjectProvider<CacheManagerCustomizer<?>> customizerObjectProvider) {
//		return new CacheManagerCustomizers(customizerObjectProvider.orderedStream().toList());
//	}
//}
