package io.hankun.framework.ai.store;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiStoreApplication
 * @createAt: 2025/10/16 10:20
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiStoreApplication.class})
public class KAiStoreApplication {
    @PostConstruct
    public void init() {
        log.info("KAiStoreApplication init");
    }
}
