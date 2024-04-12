package io.ihankun.framework.pdf.pdfbox.component.image;

import java.awt.*;

/**
 * 图片压缩模式枚举
 *
 * @author hankun
 */
public enum XEasyPdfImageScaleMode {
    /**
     * 质量
     */
    QUALITY(Image.SCALE_SMOOTH),
    /**
     * 速度
     */
    SPEED(Image.SCALE_FAST),
    /**
     * 平衡
     */
    BALANCE(Image.SCALE_DEFAULT);

    /**
     * 编码
     */
    final int code;

    /**
     * 有参构造
     *
     * @param code 编码
     */
    XEasyPdfImageScaleMode(int code) {
        this.code = code;
    }
}
