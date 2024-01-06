package io.ihankun.framework.captcha.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hankun
 *
 * 图片转换成url后的对象
 */
@Data
@NoArgsConstructor
public class ImageTransformData {

    /** 背景图. */
    private String backgroundImageUrl;

    /** 模板图. */
    private String templateImageUrl;

    /** 留一个扩展数据. */
    private Object data;

    public ImageTransformData(String backgroundImageUrl, String templateImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
        this.templateImageUrl = templateImageUrl;
    }
}
