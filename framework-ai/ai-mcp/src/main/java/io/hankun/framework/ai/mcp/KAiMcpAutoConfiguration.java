package io.hankun.framework.ai.mcp;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiMcpAutoConfiguration
 * @createAt: 2025/6/4 09:24
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiMcpAutoConfiguration.class})
public class KAiMcpAutoConfiguration {
    @PostConstruct
    public void init() {
        log.info("KAiMcpAutoConfiguration init");
    }
}
