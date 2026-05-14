package io.hankun.framework.ai.agent;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @description:
 * @className: KAiAgentApplication
 * @createAt: 2025/7/1 14:32
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@EnableScheduling
@EnableAspectJAutoProxy
@ComponentScan(basePackageClasses = {KAiAgentApplication.class})
public class KAiAgentApplication {
    @PostConstruct
    public void init() {
        log.info("KAiAgentApplication init");
    }
}
