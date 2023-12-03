package io.ihankun.framework.captcha.v2.application;

import io.ihankun.framework.common.response.ApiResponse;
import io.ihankun.framework.captcha.v1.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.v1.generator.entity.GenerateParam;
import io.ihankun.framework.captcha.v1.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.captcha.v1.validator.ImageCaptchaValidator;
import io.ihankun.framework.captcha.v1.validator.entity.ImageCaptchaTrack;
import io.ihankun.framework.captcha.v2.entity.ImageCaptchaVO;
import io.ihankun.framework.captcha.v2.enums.CaptchaImageType;
import io.ihankun.framework.captcha.v2.store.CacheStore;
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
