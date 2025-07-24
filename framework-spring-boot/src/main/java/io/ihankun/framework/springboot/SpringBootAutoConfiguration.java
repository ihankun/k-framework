package io.ihankun.framework.springboot;

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
@ConfigurationProperties(prefix = "k.spring.boot")
@ComponentScan(basePackageClasses = SpringBootAutoConfiguration.class)
public class SpringBootAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("SpringBootAutoConfiguration.init");
    }
}
