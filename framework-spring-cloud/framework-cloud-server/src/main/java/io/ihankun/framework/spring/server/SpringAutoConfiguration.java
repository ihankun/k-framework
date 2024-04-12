package io.ihankun.framework.spring.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.spring")
@EnableHystrix
@ComponentScan(basePackageClasses = SpringAutoConfiguration.class)
public class SpringAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("SpringAutoConfiguration.init");
    }
}
