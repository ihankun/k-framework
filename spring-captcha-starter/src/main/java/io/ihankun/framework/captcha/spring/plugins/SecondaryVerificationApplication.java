package io.ihankun.framework.captcha.spring.plugins;

import io.ihankun.framework.captcha.common.response.ApiResponse;
import io.ihankun.framework.captcha.build.validator.entity.ImageCaptchaTrack;
import io.ihankun.framework.captcha.spring.application.FilterImageCaptchaApplication;
import io.ihankun.framework.captcha.spring.application.ImageCaptchaApplication;
import io.ihankun.framework.captcha.spring.autoconfig.SecondaryVerificationProperties;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SecondaryVerificationApplication extends FilterImageCaptchaApplication {

    private SecondaryVerificationProperties prop;

    public SecondaryVerificationApplication(ImageCaptchaApplication target, SecondaryVerificationProperties prop) {
        super(target);
        this.prop = prop;
    }

    @Override
    public ApiResponse<?> matching(String id, ImageCaptchaTrack imageCaptchaTrack) {
        ApiResponse<?>  match = super.matching(id, imageCaptchaTrack);
        if (match.isSuccess()) {
            // 如果匹配成功， 添加二次验证记录
            addSecondaryVerification(id, imageCaptchaTrack);
        }
        return match;
    }

    /**
     * 二次缓存验证
     * @param id id
     * @return boolean
     */
    public boolean secondaryVerification(String id) {
        Map<String, Object> cache = target.getCacheStore().getAndRemoveCache(getKey(id));
        return cache != null;
    }

    /**
     * 添加二次缓存验证记录
     * @param id id
     * @param imageCaptchaTrack sliderCaptchaTrack
     */
    protected void addSecondaryVerification(String id, ImageCaptchaTrack imageCaptchaTrack) {
        target.getCacheStore().setCache(getKey(id), Collections.emptyMap(), prop.getExpire(), TimeUnit.MILLISECONDS);
    }

    protected String getKey(String id) {
        return prop.getKeyPrefix().concat(":").concat(id);
    }
}
