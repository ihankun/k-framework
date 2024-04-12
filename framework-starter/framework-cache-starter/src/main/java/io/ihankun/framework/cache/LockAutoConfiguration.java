package io.ihankun.framework.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.lock")
@ComponentScan(basePackageClasses = LockAutoConfiguration.class)
public class LockAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("LockAutoConfiguration.init");
    }
}
