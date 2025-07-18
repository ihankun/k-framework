package io.ihankun.framework.commons;

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
@ConfigurationProperties(prefix = "k.commons")
@ComponentScan(basePackageClasses = CommonsAutoConfiguration.class)
public class CommonsAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("CommonsAutoConfiguration.init");
    }
}
