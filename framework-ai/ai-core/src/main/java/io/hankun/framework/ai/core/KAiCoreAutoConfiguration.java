package io.hankun.framework.ai.core;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiCommonAutoConfiguration
 * @createAt: 2025/5/29 14:11
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiCoreAutoConfiguration.class})
public class KAiCoreAutoConfiguration {
    @PostConstruct
    public void init() {
        log.info("KAiCommonAutoConfiguration.init.start");
    }
}
