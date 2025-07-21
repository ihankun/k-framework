package io.ihankun.framework.websocket;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.websocket")
@ComponentScan(basePackageClasses = WebsocketAutoConfiguration.class)
public class WebsocketAutoConfiguration {
    @PostConstruct
    public void init() {
        log.info("WebsocketAutoConfiguration.init");
    }
}
