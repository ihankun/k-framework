package io.ihankun.framework.captcha.build.validator;


import io.ihankun.framework.captcha.common.response.ApiResponse;
import io.ihankun.framework.captcha.build.generator.entity.ImageCaptchaInfo;
import io.ihankun.framework.captcha.build.validator.entity.ImageCaptchaTrack;

import java.util.Map;

/**
 * @Author: hankun
 * @date 2022/2/17 10:54
 * @Description 图片验证码校验器
 */
public interface ImageCaptchaValidator {

    /**
     * 用于生成验证码校验时需要的回传参数
     *
     * @param imageCaptchaInfo 生成的验证码数据
     * @return Map<String, Object>
     */
    Map<String, Object> generateImageCaptchaValidData(ImageCaptchaInfo imageCaptchaInfo);

    /**
     * 校验用户滑动滑块是否正确
     *
     * @param imageCaptchaTrack      包含了滑动轨迹，展示的图片宽高，滑动时间等参数
     * @param imageCaptchaValidData generateImageCaptchaValidData(生成的数据)
     * @return ApiResponse<?>
     */
    ApiResponse<?> valid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> imageCaptchaValidData);
}
