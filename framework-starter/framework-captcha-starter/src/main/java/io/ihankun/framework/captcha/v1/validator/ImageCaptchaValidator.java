package io.ihankun.framework.captcha.v1.validator;

import io.ihankun.framework.captcha.v1.entity.ImageCaptchaInfo;
import io.ihankun.framework.captcha.v1.entity.ImageCaptchaTrack;
import io.ihankun.framework.core.response.ResponseResult;

import java.util.Map;

/**
 * @author hankun
 *
 * 图片验证码校验器
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
    ResponseResult<?> valid(ImageCaptchaTrack imageCaptchaTrack, Map<String, Object> imageCaptchaValidData);
}
