package io.hankun.framework.ai.agent.api;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiAgentApiApplication
 * @createAt: 2025/11/14 11:53
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiAgentApiApplication.class})
public class KAiAgentApiApplication {

    @PostConstruct
    public void init() {
        log.info("KAiAgentApiApplication init");
    }
}
