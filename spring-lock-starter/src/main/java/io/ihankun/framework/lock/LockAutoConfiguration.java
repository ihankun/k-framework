package io.ihankun.framework.lock;

import io.ihankun.framework.cache.CacheBuilder;
import io.ihankun.framework.cache.CacheManager;
import io.ihankun.framework.cache.CacheType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.lock")
@ComponentScan(basePackageClasses = LockAutoConfiguration.class)
public class LockAutoConfiguration {

    /**
     * 为请求锁的接口声明通用的缓存管理器
     */
    @ConditionalOnMissingBean
    @Order
    @Bean
    public CacheManager<String, String> RequestLockCacheManager() {
        return CacheBuilder.build(CacheType.REDIS);
    }


    @PostConstruct
    public void init() {
        log.info("LockAutoConfiguration.init");
    }
}
