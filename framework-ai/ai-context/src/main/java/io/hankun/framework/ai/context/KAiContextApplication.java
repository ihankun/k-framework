package io.hankun.framework.ai.context;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiContextApplication
 * @createAt: 2025/10/16 10:45
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiContextApplication.class})
public class KAiContextApplication {

    @PostConstruct
    public void init() {
        log.info("KAiContextApplication init");
    }
}
