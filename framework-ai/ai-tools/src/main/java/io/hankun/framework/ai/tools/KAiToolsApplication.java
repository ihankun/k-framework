package io.hankun.framework.ai.tools;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiToolsApplication
 * @createAt: 2025/10/16 13:35
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiToolsApplication.class})
public class KAiToolsApplication {

    @PostConstruct
    public void init() {
        log.info("KAiToolsApplication init");
    }
}
