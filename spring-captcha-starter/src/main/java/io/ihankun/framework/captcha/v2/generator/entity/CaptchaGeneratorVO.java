package io.ihankun.framework.captcha.v2.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaGeneratorVO {

    /** 背景图. */
    private String backgroundImage;

    /** 模板图. */
    private String templateImage;

    /** 随机值. */
    private Integer randomX;

    /** 容错值, 可以为空 默认 0.02容错,校验的时候用. */
    private Float tolerant;

}
