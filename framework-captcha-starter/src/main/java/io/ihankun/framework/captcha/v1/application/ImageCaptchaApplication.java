package io.ihankun.framework.captcha.v1.application;

import io.ihankun.framework.captcha.v1.generator.ImageCaptchaGenerator;
import io.ihankun.framework.captcha.v1.entity.GenerateParam;
import io.ihankun.framework.captcha.v1.resource.ImageCaptchaResourceManager;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaTrack;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaVO;
import io.ihankun.framework.captcha.v1.validator.ImageCaptchaValidator;
import io.ihankun.framework.captcha.v1.enums.CaptchaImageType;
import io.ihankun.framework.captcha.v1.store.CacheStore;
import io.ihankun.framework.common.response.ResponseCaptcha;
import io.ihankun.framework.common.response.ResponseResult;

/**
 * @author hankun
 */
public interface ImageCaptchaApplication {

    /**
     * 生成滑块验证码
     *
     * @return
     */
    ResponseCaptcha<ImageCaptchaVO> generateCaptcha();

    /**
     * 生成滑块验证码
     *
     * @param type type类型
     * @return CaptchaResponse<SliderCaptchaVO>
     */
    ResponseCaptcha<ImageCaptchaVO> generateCaptcha(String type);

    /**
     * 生成滑块验证码
     *
     * @param captchaImageType 要生成webp还是jpg类型的图片
     * @return CaptchaResponse<SliderCaptchaVO>
     */
    ResponseCaptcha<ImageCaptchaVO> generateCaptcha(CaptchaImageType captchaImageType);

    /**
     * 生成验证码
     *
     * @param type             type
     * @param captchaImageType CaptchaImageType
     * @return CaptchaResponse<ImageCaptchaVO>
     */
    ResponseCaptcha<ImageCaptchaVO> generateCaptcha(String type, CaptchaImageType captchaImageType);


    /**
     * 生成滑块验证码
     *
     * @param param param
     * @return CaptchaResponse<SliderCaptchaVO>
     */
    ResponseCaptcha<ImageCaptchaVO> generateCaptcha(GenerateParam param);

    /**
     * 匹配
     *
     * @param id                验证码的ID
     * @param imageCaptchaTrack 滑动轨迹
     * @return 匹配成功返回true， 否则返回false
     */
    ResponseResult<?> matching(String id, ImageCaptchaTrack imageCaptchaTrack);

    /**
     * 兼容一下旧版本，新版本建议使用 {@link ImageCaptchaApplication#matching(String, ImageCaptchaTrack)}
     * @param id id
     * @param percentage 百分比数据
     * @return boolean
     *
     */
    @Deprecated
    boolean matching(String id, Float percentage);

    /**
     * 获取验证码资源管理器
     *
     * @return SliderCaptchaResourceManager
     */
    ImageCaptchaResourceManager getImageCaptchaResourceManager();

    /**
     * 设置 SliderCaptchaValidator 验证码验证器
     *
     * @param imageCaptchaValidator imageCaptchaValidator
     */
    void setImageCaptchaValidator(ImageCaptchaValidator imageCaptchaValidator);

    /**
     * 设置 ImageCaptchaGenerator 验证码生成器
     *
     * @param imageCaptchaGenerator SliderCaptchaGenerator
     */
    void setImageCaptchaTemplate(ImageCaptchaGenerator imageCaptchaGenerator);

    /**
     * 设置 缓存存储器
     *
     * @param cacheStore cacheStore
     */
    void setCacheStore(CacheStore cacheStore);

    /**
     * 获取验证码验证器
     *
     * @return SliderCaptchaValidator
     */
    ImageCaptchaValidator getImageCaptchaValidator();

    /**
     * 获取验证码生成器
     *
     * @return SliderCaptchaTemplate
     */
    ImageCaptchaGenerator getImageCaptchaTemplate();

    /**
     * 获取缓存存储器
     *
     * @return CacheStore
     */
    CacheStore getCacheStore();
}
