package io.hankun.framework.redis;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "k.redis")
@ComponentScan(basePackageClasses = CacheAutoConfiguration.class)
public class CacheAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("CacheAutoConfiguration.init");
    }
}
