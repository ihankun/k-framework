package io.hankun.framework.captcha;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hankun
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "spring.captcha")
@ComponentScan(basePackageClasses = CaptchaAutoConfig.class)
public class CaptchaAutoConfig {

    @PostConstruct
    public void init() {
        log.info("captcha -- CaptchaAutoConfig.init");
    }
}
