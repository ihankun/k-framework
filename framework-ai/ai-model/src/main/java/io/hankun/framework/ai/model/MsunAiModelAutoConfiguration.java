package io.hankun.framework.ai.model;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: MsunAiModelAutoConfiguration
 * @createAt: 2025/7/16 10:34
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {MsunAiModelAutoConfiguration.class})
public class MsunAiModelAutoConfiguration {
    @PostConstruct
    public void init() {
        log.info("MsunAiModelAutoConfiguration.init.start");
    }
}
