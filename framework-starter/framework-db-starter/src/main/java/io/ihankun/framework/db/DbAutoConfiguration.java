package io.ihankun.framework.db;

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
@ConfigurationProperties(prefix = "kun.db")
@ComponentScan(basePackageClasses = DbAutoConfiguration.class)
public class DbAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("DbAutoConfiguration.init");
    }
}
