package io.ihankun.framework.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.ai")
@ComponentScan(basePackageClasses = AiAutoConfiguration.class)
public class AiAutoConfiguration {
    @PostConstruct
    public void init() {
        log.info("AiAutoConfiguration.init");
    }
}
