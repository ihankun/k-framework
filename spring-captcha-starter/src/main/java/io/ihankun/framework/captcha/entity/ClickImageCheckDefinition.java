package io.ihankun.framework.captcha.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * @author hankun
 *
 * 点击图片校验描述
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickImageCheckDefinition {

    /** 提示.*/
    private Resource tip;

    /** x.*/
    private Integer x;

    /** y.*/
    private Integer y;

    /** 宽.*/
    private Integer width;

    /** 高.*/
    private Integer height;

    /** 颜色.*/
    private Color imageColor;

}
