package io.hankun.framework.ai.mem;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: MsunAiMemApplication
 * @createAt: 2025/12/3 11:51
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {MsunAiMemApplication.class})
public class MsunAiMemApplication {

    @PostConstruct
    public void init() {
        log.info("MsunAiMemApplication init");
    }
}
