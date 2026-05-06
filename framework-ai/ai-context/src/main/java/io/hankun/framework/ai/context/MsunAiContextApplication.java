package io.hankun.framework.ai.context;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: MsunAiContextApplication
 * @createAt: 2025/10/16 10:45
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {MsunAiContextApplication.class})
public class MsunAiContextApplication {

    @PostConstruct
    public void init() {
        log.info("MsunAiContextApplication init");
    }
}
