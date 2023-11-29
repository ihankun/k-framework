package io.ihankun.framework.captcha.spring.autoconfig;

import io.ihankun.framework.captcha.build.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.build.generator.impl.CacheImageCaptchaGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class CacheCaptchaTemplateListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        try {
            ImageCaptchaGenerator bean = applicationContext.getBean(ImageCaptchaGenerator.class);
            if (bean instanceof CacheImageCaptchaGenerator) {
                CacheImageCaptchaGenerator cacheSliderCaptchaTemplate = (CacheImageCaptchaGenerator) bean;
                // 初始化调度器
                cacheSliderCaptchaTemplate.initSchedule();
            }
        } catch (BeansException e) {
            log.debug("CaptchaTemplateListener 获取 SliderCaptchaTemplate 失败");
        }
    }
}
