package io.ihankun.framework.job;

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
@ConfigurationProperties(prefix = "kun.job")
@ComponentScan(basePackageClasses = JobAutoConfiguration.class)
public class JobAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("JobAutoConfiguration.init");
    }
}
