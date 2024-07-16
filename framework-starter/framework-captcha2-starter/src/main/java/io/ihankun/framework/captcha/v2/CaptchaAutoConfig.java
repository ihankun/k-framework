package io.ihankun.framework.captcha.v2;

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
@ConfigurationProperties(prefix = "k.captcha")
@ComponentScan(basePackageClasses = CaptchaAutoConfig.class)
public class CaptchaAutoConfig {

    @PostConstruct
    public void init() {
        log.info("captcha2 -- CaptchaAutoConfig.init");
    }
}
