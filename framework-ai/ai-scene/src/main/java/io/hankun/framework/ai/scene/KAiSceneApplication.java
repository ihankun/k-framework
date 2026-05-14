package io.hankun.framework.ai.scene;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @className: KAiSceneApplication
 * @createAt: 2025/10/16 10:45
 * @author: hankun
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackageClasses = {KAiSceneApplication.class})
public class KAiSceneApplication {

    @PostConstruct
    public void init() {
        log.info("KAiSceneApplication init");
    }

}
