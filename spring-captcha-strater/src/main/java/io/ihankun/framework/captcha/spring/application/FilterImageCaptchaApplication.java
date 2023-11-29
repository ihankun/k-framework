package io.ihankun.framework.captcha.spring.application;

import io.ihankun.framework.captcha.common.response.ApiResponse;
import io.ihankun.framework.captcha.build.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.build.generator.entity.GenerateParam;
import io.ihankun.framework.captcha.build.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.captcha.build.validator.ImageCaptchaValidator;
import io.ihankun.framework.captcha.build.validator.entity.ImageCaptchaTrack;
import io.ihankun.framework.captcha.spring.entity.ImageCaptchaVO;
import io.ihankun.framework.captcha.spring.enums.CaptchaImageType;
import io.ihankun.framework.captcha.spring.store.CacheStore;
import io.ihankun.framework.common.response.CaptchaResponse;

public class FilterImageCaptchaApplication implements ImageCaptchaApplication{

    protected ImageCaptchaApplication target;

    public FilterImageCaptchaApplication(ImageCaptchaApplication target) {
        this.target = target;
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha() {
        return target.generateCaptcha();
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type) {
        return target.generateCaptcha(type);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(CaptchaImageType captchaImageType) {
        return target.generateCaptcha(captchaImageType);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(String type, CaptchaImageType captchaImageType) {
        return target.generateCaptcha(type, captchaImageType);
    }

    @Override
    public CaptchaResponse<ImageCaptchaVO> generateCaptcha(GenerateParam param) {
        return target.generateCaptcha(param);
    }

    @Override
    public ApiResponse<?> matching(String id, ImageCaptchaTrack ImageCaptchaTrack) {
        return target.matching(id, ImageCaptchaTrack);
    }

    @Override
    public boolean matching(String id, Float percentage) {
        return target.matching(id, percentage);
    }

    @Override
    public ImageCaptchaResourceManager getImageCaptchaResourceManager() {
        return target.getImageCaptchaResourceManager();
    }

    @Override
    public void setImageCaptchaValidator(ImageCaptchaValidator sliderCaptchaValidator) {
        target.setImageCaptchaValidator(sliderCaptchaValidator);
    }

    @Override
    public void setImageCaptchaTemplate(ImageCaptchaGenerator imageCaptchaGenerator) {
        target.setImageCaptchaTemplate(imageCaptchaGenerator);
    }

    @Override
    public void setCacheStore(CacheStore cacheStore) {
        target.setCacheStore(cacheStore);
    }

    @Override
    public ImageCaptchaValidator getImageCaptchaValidator() {
        return target.getImageCaptchaValidator();
    }

    @Override
    public ImageCaptchaGenerator getImageCaptchaTemplate() {
        return target.getImageCaptchaTemplate();
    }

    @Override
    public CacheStore getCacheStore() {
        return target.getCacheStore();
    }
}
