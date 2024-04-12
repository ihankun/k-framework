package io.ihankun.framework.captcha.v2.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hankun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaGeneratorVO {

    /** 背景图. */
    private String bgImage;

    /** 模板图. */
    private String tmpImage;

    /** 随机值. */
    private Integer randomX;

    /** 容错值, 可以为空 默认 0.02容错,校验的时候用. */
    private Float tolerant;

    /** 百分比 */
    private Float percentage;

}
