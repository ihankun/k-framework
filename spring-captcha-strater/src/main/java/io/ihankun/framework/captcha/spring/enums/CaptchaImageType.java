package io.ihankun.framework.captcha.spring.enums;

import lombok.Getter;

@Getter
public enum CaptchaImageType {

    WEBP,
    /** jpg+png类型. */
    JPEG_PNG;

    public static CaptchaImageType getType(String bgImageType, String sliderImageType) {
        if ("webp".equalsIgnoreCase(bgImageType) && "webp".equalsIgnoreCase(sliderImageType)) {
            return WEBP;
        }
        if (("jpeg".equalsIgnoreCase(bgImageType) || "jpg".equalsIgnoreCase(bgImageType)) && "png".equalsIgnoreCase(sliderImageType)) {
            return JPEG_PNG;
        }
        return null;
    }
}
