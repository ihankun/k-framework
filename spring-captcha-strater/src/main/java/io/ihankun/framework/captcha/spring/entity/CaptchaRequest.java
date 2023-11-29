package io.ihankun.framework.captcha.spring.entity;

import io.ihankun.framework.captcha.build.validator.entity.ImageCaptchaTrack;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CaptchaRequest<T> {

    @NotEmpty(message = "验证码ID不能为空")
    private String id;

    @NotNull(message = "滑动轨迹不能为空")
    private ImageCaptchaTrack captchaTrack;

    @Valid
    private T form;
}
