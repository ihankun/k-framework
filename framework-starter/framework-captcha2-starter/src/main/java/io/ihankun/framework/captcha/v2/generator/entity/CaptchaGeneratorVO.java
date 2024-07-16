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

    /** x轴百分比 */
    private Float x;

}
