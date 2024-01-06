package io.ihankun.framework.captcha.generator;


import io.ihankun.framework.captcha.entity.CaptchaExchange;
import io.ihankun.framework.captcha.entity.ImageCaptchaInfo;

/**
 * @author hankun
 *
 * 图片验证码后处理器
 */
public interface ImageCaptchaPostProcessor {

    /**
     * 在生成验证码核心逻辑之前调用， 用于拦截验证码生成、限流、自定义返回数据等处理
     *
     * @param captchaExchange 传输数据
     * @param generator    generator
     * @return ImageCaptchaInfo
     */
    default ImageCaptchaInfo beforeGenerateCaptchaImage(CaptchaExchange captchaExchange, ImageCaptchaGenerator generator) {
        return null;
    }

    /**
     * 在执行包装 ImageCaptchaInfo 核心逻辑之前处理
     *
     * @param captchaExchange 传输数据
     * @param generator    generator
     */
    default void beforeWrapImageCaptchaInfo(CaptchaExchange captchaExchange, ImageCaptchaGenerator generator) {

    }

    /**
     * 在执行包装 ImageCaptchaInfo 核心逻辑之后处理
     *
     * @param captchaExchange     captchaExchange
     * @param imageCaptchaInfo imageCaptchaInfo
     * @param generator        generator
     */
    default void afterGenerateCaptchaImage(CaptchaExchange captchaExchange, ImageCaptchaInfo imageCaptchaInfo, ImageCaptchaGenerator generator) {

    }
}
