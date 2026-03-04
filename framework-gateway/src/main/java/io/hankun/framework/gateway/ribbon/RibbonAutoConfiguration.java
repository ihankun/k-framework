package io.hankun.framework.gateway.ribbon;

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
@ConfigurationProperties(prefix = "kun.ribbon")
@ComponentScan(basePackageClasses = RibbonAutoConfiguration.class)
public class RibbonAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("RibbonAutoConfiguration.init");
    }
}
