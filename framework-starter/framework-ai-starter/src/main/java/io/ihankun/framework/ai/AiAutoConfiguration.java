package io.ihankun.framework.ai;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
