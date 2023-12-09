package io.ihankun.framework.captcha;

import io.ihankun.framework.captcha.CacheStoreAutoConfiguration;
import io.ihankun.framework.captcha.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.generator.ImageTransform;
import io.ihankun.framework.captcha.generator.impl.CacheImageCaptchaGenerator;
import io.ihankun.framework.captcha.generator.impl.transform.Base64ImageTransform;
import io.ihankun.framework.captcha.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.captcha.resource.ResourceStore;
import io.ihankun.framework.captcha.resource.impl.DefaultImageCaptchaResourceManager;
import io.ihankun.framework.captcha.resource.impl.DefaultResourceStore;
import io.ihankun.framework.captcha.validator.ImageCaptchaValidator;
import io.ihankun.framework.captcha.validator.impl.BasicCaptchaTrackValidator;
import io.ihankun.framework.captcha.aop.CaptchaAdvisor;
import io.ihankun.framework.captcha.aop.CaptchaInterceptor;
import io.ihankun.framework.captcha.application.DefaultImageCaptchaApplication;
import io.ihankun.framework.captcha.application.ImageCaptchaApplication;
import io.ihankun.framework.captcha.listener.CacheCaptchaTemplateListener;
import io.ihankun.framework.captcha.config.ImageCaptchaProperties;
import io.ihankun.framework.captcha.config.SliderCaptchaCacheProperties;
import io.ihankun.framework.captcha.plugins.SecondaryVerificationApplication;
import io.ihankun.framework.captcha.plugins.SpringMultiImageCaptchaGenerator;
import io.ihankun.framework.captcha.store.CacheStore;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;

/**
 * @author hankun
 */
@Order
@Configuration
@AutoConfigureAfter(CacheStoreAutoConfiguration.class)
@EnableConfigurationProperties({ImageCaptchaProperties.class})
public class ImageCaptchaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ResourceStore resourceStore() {
        return new DefaultResourceStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaResourceManager imageCaptchaResourceManager(ResourceStore resourceStore) {
        return new DefaultImageCaptchaResourceManager(resourceStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageTransform imageTransform() {
        return new Base64ImageTransform();
    }


    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaGenerator imageCaptchaTemplate(ImageCaptchaProperties prop,
                                                      ImageCaptchaResourceManager captchaResourceManager,
                                                      ImageTransform imageTransform,
                                                      BeanFactory beanFactory) {
        // 构建多验证码生成器
        ImageCaptchaGenerator captchaGenerator = new SpringMultiImageCaptchaGenerator(captchaResourceManager, imageTransform, beanFactory);
        SliderCaptchaCacheProperties cache = prop.getCache();
        if (cache != null && Boolean.TRUE.equals(cache.getEnabled()) && cache.getCacheSize() > 0) {
            // 增加缓存处理
            captchaGenerator = new CacheImageCaptchaGenerator(captchaGenerator, cache.getCacheSize(), cache.getWaitTime(), cache.getPeriod());
        }
        // 初始化
        captchaGenerator.init(prop.getInitDefaultResource());
        return captchaGenerator;
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaValidator imageCaptchaValidator() {
        return new BasicCaptchaTrackValidator();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public CaptchaInterceptor captchaInterceptor() {
        return new CaptchaInterceptor();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public CaptchaAdvisor captchaAdvisor(CaptchaInterceptor interceptor) {
        return new CaptchaAdvisor(interceptor);
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public CacheCaptchaTemplateListener captchaTemplateListener() {
        return new CacheCaptchaTemplateListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImageCaptchaApplication imageCaptchaApplication(ImageCaptchaGenerator captchaGenerator,
                                                           ImageCaptchaValidator imageCaptchaValidator,
                                                           CacheStore cacheStore,
                                                           ImageCaptchaProperties prop) {
        ImageCaptchaApplication target = new DefaultImageCaptchaApplication(captchaGenerator, imageCaptchaValidator, cacheStore, prop);
        if (prop.getSecondary() != null && Boolean.TRUE.equals(prop.getSecondary().getEnabled())) {
            target = new SecondaryVerificationApplication(target, prop.getSecondary());
        }
        return target;
    }
}
