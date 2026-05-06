package io.hankun.framework.ai.common;

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
@ComponentScan(basePackageClasses = {KAiCommonAutoConfiguration.class})
public class KAiCommonAutoConfiguration {
    @PostConstruct
    public void init() {
        log.info("KAiCommonAutoConfiguration.init.start");
    }
}
