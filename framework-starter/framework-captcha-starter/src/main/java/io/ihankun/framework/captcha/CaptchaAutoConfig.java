package io.ihankun.framework.captcha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

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
