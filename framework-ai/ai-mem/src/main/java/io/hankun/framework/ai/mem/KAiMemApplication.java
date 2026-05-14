package io.hankun.framework.ai.mem;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiMemApplication
 * @createAt: 2025/12/3 11:51
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiMemApplication.class})
public class KAiMemApplication {

    @PostConstruct
    public void init() {
        log.info("KAiMemApplication init");
    }
}
