package io.ihankun.framework.core;

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
@ConfigurationProperties(prefix = "kun.base")
@ComponentScan(basePackageClasses = BaseAutoConfiguration.class)
public class BaseAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("BaseAutoConfiguration.init");
    }
}
