package io.ihankun.framework.captcha.v1.application;

import io.ihankun.framework.captcha.v1.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.v1.entity.GenerateParam;
import io.ihankun.framework.captcha.v1.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaTrack;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaVO;
import io.ihankun.framework.captcha.v1.enums.CaptchaImageType;
import io.ihankun.framework.captcha.v1.store.CacheStore;
import io.ihankun.framework.captcha.v1.validator.ImageCaptchaValidator;
import io.ihankun.framework.common.v1.response.ResponseCaptcha;
import io.ihankun.framework.common.v1.response.ResponseResult;

/**
 * @author hankun
 */
public class FilterImageCaptchaApplication implements ImageCaptchaApplication{

    protected ImageCaptchaApplication target;

    public FilterImageCaptchaApplication(ImageCaptchaApplication target) {
        this.target = target;
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha() {
        return target.generateCaptcha();
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha(String type) {
        return target.generateCaptcha(type);
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha(CaptchaImageType captchaImageType) {
        return target.generateCaptcha(captchaImageType);
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha(String type, CaptchaImageType captchaImageType) {
        return target.generateCaptcha(type, captchaImageType);
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha(GenerateParam param) {
        return target.generateCaptcha(param);
    }

    @Override
    public ResponseResult<?> matching(String id, ImageCaptchaTrack ImageCaptchaTrack) {
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
