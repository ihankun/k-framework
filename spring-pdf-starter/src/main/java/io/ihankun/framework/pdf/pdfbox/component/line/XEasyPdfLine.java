package io.ihankun.framework.pdf.pdfbox.component.line;

import io.ihankun.framework.pdf.pdfbox.component.XEasyPdfComponent;
import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfDefaultFontStyle;

import java.awt.*;

/**
 * pdf线条组件
 *
 * @author hankun
 */
public interface XEasyPdfLine extends XEasyPdfComponent {

    /**
     * 设置字体路径
     *
     * @param fontPath 字体路径
     * @return 返回线条组件
     */
    XEasyPdfLine setFontPath(String fontPath);

    /**
     * 设置默认字体样式
     *
     * @param style 默认字体样式
     * @return 返回线条组件
     */
    XEasyPdfLine setDefaultFontStyle(XEasyPdfDefaultFontStyle style);

    /**
     * 设置左边距
     *
     * @param margin 边距
     * @return 返回线条组件
     */
    XEasyPdfLine setMarginLeft(float margin);

    /**
     * 设置右边距
     *
     * @param margin 边距
     * @return 返回线条组件
     */
    XEasyPdfLine setMarginRight(float margin);

    /**
     * 设置线条宽度
     *
     * @param lineWidth 线条宽度
     * @return 返回线条组件
     */
    XEasyPdfLine setLineWidth(float lineWidth);

    /**
     * 设置线条颜色
     *
     * @param color 线条颜色
     * @return 返回线条组件
     */
    XEasyPdfLine setColor(Color color);

    /**
     * 设置线条线型
     *
     * @param lineCapStyle 线条线型
     * @return 返回线条组件
     */
    XEasyPdfLine setLineCapStyle(XEasyPdfLineCapStyle lineCapStyle);

    /**
     * 获取线条宽度
     *
     * @return 返回线条宽度
     */
    float getLineWidth();
}
