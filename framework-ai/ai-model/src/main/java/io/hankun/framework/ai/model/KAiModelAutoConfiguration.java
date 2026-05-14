package io.hankun.framework.ai.model;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiModelAutoConfiguration
 * @createAt: 2025/7/16 10:34
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiModelAutoConfiguration.class})
public class KAiModelAutoConfiguration {
    @PostConstruct
    public void init() {
        log.info("KAiModelAutoConfiguration.init.start");
    }
}
