package io.ihankun.framework.springcloud.server;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "k.spring.cloud")
@EnableHystrix
@ComponentScan(basePackageClasses = SpringCloudAutoConfiguration.class)
public class SpringCloudAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("SpringCloudAutoConfiguration.init");
    }
}
