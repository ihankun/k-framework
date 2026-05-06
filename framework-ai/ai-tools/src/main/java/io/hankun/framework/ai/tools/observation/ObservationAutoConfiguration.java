package io.hankun.framework.ai.tools.observation;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @className: ObservationAutoConfiguration
 * @createAt: 2025/10/11 17:01
 * @author: hankun
 */
@Configuration
public class ObservationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }

}
