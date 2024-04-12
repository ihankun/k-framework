package io.ihankun.framework.core;

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
@ConfigurationProperties(prefix = "kun.common")
@ComponentScan(basePackageClasses = CommonAutoConfiguration.class)
public class CommonAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("CommonAutoConfiguration.init");
    }
}
