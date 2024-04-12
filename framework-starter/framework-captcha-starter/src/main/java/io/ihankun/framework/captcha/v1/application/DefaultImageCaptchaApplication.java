package io.ihankun.framework.captcha.v1.application;

import io.ihankun.framework.captcha.v1.config.ImageCaptchaProperties;
import io.ihankun.framework.captcha.v1.entity.GenerateParam;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaInfo;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaTrack;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaVO;
import io.ihankun.framework.captcha.v1.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.core.error.impl.CaptchaErrorCode;
import io.ihankun.framework.captcha.v1.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.v1.validator.ImageCaptchaValidator;
import io.ihankun.framework.captcha.v1.validator.impl.SimpleImageCaptchaValidator;
import io.ihankun.framework.captcha.v1.enums.CaptchaImageType;
import io.ihankun.framework.captcha.v1.store.CacheStore;
import io.ihankun.framework.core.constants.captcha.CaptchaTypeConstant;
import io.ihankun.framework.core.exception.CaptchaValidException;
import io.ihankun.framework.core.exception.ImageCaptchaException;
import io.ihankun.framework.core.response.ResponseCaptcha;
import io.ihankun.framework.core.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Slf4j
public class DefaultImageCaptchaApplication implements ImageCaptchaApplication{

    /** 图片验证码生成器. */
    private ImageCaptchaGenerator template;

    /** 图片验证码校验器. */
    private ImageCaptchaValidator imageCaptchaValidator;

    /** 缓冲存储. */
    private CacheStore cacheStore;

    /** 验证码配置属性. */
    private final ImageCaptchaProperties prop;

    /** 默认的过期时间. */
    private long defaultExpire = 20000L;

    public DefaultImageCaptchaApplication(ImageCaptchaGenerator template,
                                          ImageCaptchaValidator imageCaptchaValidator,
                                          CacheStore cacheStore,
                                          ImageCaptchaProperties prop) {
        this.prop = prop;
        setImageCaptchaTemplate(template);
        setImageCaptchaValidator(imageCaptchaValidator);
        setCacheStore(cacheStore);
        // 默认过期时间
        Long defaultExpire = prop.getExpire().get("default");
        if (defaultExpire != null && defaultExpire > 0) {
            this.defaultExpire = defaultExpire;
        }
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha() {
        // 生成滑块验证码
        return generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha(String type) {
        ImageCaptchaInfo slideImageInfo = getImageCaptchaTemplate().generateCaptchaImage(type);
        return afterGenerateSliderCaptcha(slideImageInfo);
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha(GenerateParam param) {
        ImageCaptchaInfo slideImageInfo = getImageCaptchaTemplate().generateCaptchaImage(param);
        return afterGenerateSliderCaptcha(slideImageInfo);
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha(CaptchaImageType captchaImageType) {
        return generateCaptcha(CaptchaTypeConstant.SLIDER, captchaImageType);
    }

    @Override
    public ResponseCaptcha<ImageCaptchaVO> generateCaptcha(String type, CaptchaImageType captchaImageType) {
        GenerateParam param = new GenerateParam();
        if (CaptchaImageType.WEBP.equals(captchaImageType)) {
            param.setBackgroundFormatName("webp");
            param.setTemplateFormatName("webp");
        } else {
            param.setBackgroundFormatName("jpeg");
            param.setTemplateFormatName("png");
        }
        param.setType(type);
        return generateCaptcha(param);
    }


    public ResponseCaptcha<ImageCaptchaVO> afterGenerateSliderCaptcha(ImageCaptchaInfo slideImageInfo) {
        if (slideImageInfo == null) {
            // 要是生成失败
            throw new ImageCaptchaException("生成滑块验证码失败，验证码生成为空");
        }
        // 生成ID
        String id = generatorId();
        // 生成校验数据
        Map<String, Object> validData = getImageCaptchaValidator().generateImageCaptchaValidData(slideImageInfo);
        // 存到缓存里
        cacheVerification(id, slideImageInfo.getType(), validData);
        ImageCaptchaVO verificationVO = new ImageCaptchaVO();
        verificationVO.setType(slideImageInfo.getType());
        verificationVO.setBackgroundImage(slideImageInfo.getBackgroundImage());
        verificationVO.setTemplateImage(slideImageInfo.getTemplateImage());
        verificationVO.setBackgroundImageTag(slideImageInfo.getBackgroundImageTag());
        verificationVO.setTemplateImageTag(slideImageInfo.getTemplateImageTag());
        verificationVO.setBackgroundImageWidth(slideImageInfo.getBackgroundImageWidth());
        verificationVO.setBackgroundImageHeight(slideImageInfo.getBackgroundImageHeight());
        verificationVO.setTemplateImageWidth(slideImageInfo.getTemplateImageWidth());
        verificationVO.setTemplateImageHeight(slideImageInfo.getTemplateImageHeight());
        verificationVO.setData(slideImageInfo.getData() == null ? null : slideImageInfo.getData().getViewData());
        return ResponseCaptcha.of(id, verificationVO);
    }

    @Override
    public ResponseResult<?> matching(String id, ImageCaptchaTrack imageCaptchaTrack) {
        Map<String, Object> cachePercentage = getVerification(id);
        if (cachePercentage == null) {
            return ResponseResult.error(CaptchaErrorCode.EXPIRED);
        }
        return getImageCaptchaValidator().valid(imageCaptchaTrack, cachePercentage);
    }

    @Override
    public boolean matching(String id, Float percentage) {
        Map<String, Object> cachePercentage = getVerification(id);
        if (cachePercentage == null) {
            return false;
        }
        ImageCaptchaValidator imageCaptchaValidator = getImageCaptchaValidator();
        if (!(imageCaptchaValidator instanceof SimpleImageCaptchaValidator)) {
            return false;
        }
        SimpleImageCaptchaValidator simpleImageCaptchaValidator = (SimpleImageCaptchaValidator) imageCaptchaValidator;
        Float oriPercentage = simpleImageCaptchaValidator.getFloatParam(SimpleImageCaptchaValidator.PERCENTAGE_KEY, cachePercentage);
        // 读容错值
        Float tolerant = simpleImageCaptchaValidator.getFloatParam(SimpleImageCaptchaValidator.TOLERANT_KEY, cachePercentage, simpleImageCaptchaValidator.getDefaultTolerant());
        return simpleImageCaptchaValidator.checkPercentage(percentage, oriPercentage, tolerant);
    }

    protected String generatorId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 通过缓存获取百分比
     *
     * @param id 验证码ID
     * @return Map<String, Object>
     */
    protected Map<String, Object> getVerification(String id) {
        return getCacheStore().getAndRemoveCache(getKey(id));
    }

    /**
     * 缓存验证码
     *
     * @param id        id
     * @param type
     * @param validData validData
     */
    protected void cacheVerification(String id, String type, Map<String, Object> validData) {
        Long expire = prop.getExpire().getOrDefault(type, defaultExpire);
        if (!getCacheStore().setCache(getKey(id), validData, expire, TimeUnit.MILLISECONDS)) {
            log.error("缓存验证码数据失败， id={}, validData={}", id, validData);
            throw new CaptchaValidException(type, "缓存验证码数据失败");
        }
    }

    protected String getKey(String id) {
        return prop.getPrefix().concat(":").concat(id);
    }

    @Override
    public ImageCaptchaResourceManager getImageCaptchaResourceManager() {
        return getImageCaptchaTemplate().getImageResourceManager();
    }

    @Override
    public void setImageCaptchaValidator(ImageCaptchaValidator imageCaptchaValidator) {
        this.imageCaptchaValidator = imageCaptchaValidator;
    }

    @Override
    public void setImageCaptchaTemplate(ImageCaptchaGenerator imageCaptchaGenerator) {
        this.template = imageCaptchaGenerator;
    }

    @Override
    public void setCacheStore(CacheStore cacheStore) {
        this.cacheStore = cacheStore;
    }

    @Override
    public ImageCaptchaValidator getImageCaptchaValidator() {
        return this.imageCaptchaValidator;
    }

    @Override
    public ImageCaptchaGenerator getImageCaptchaTemplate() {
        return this.template;
    }

    @Override
    public CacheStore getCacheStore() {
        return this.cacheStore;
    }
}
