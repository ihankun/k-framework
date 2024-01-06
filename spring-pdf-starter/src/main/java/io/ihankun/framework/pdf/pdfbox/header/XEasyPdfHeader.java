package io.ihankun.framework.pdf.pdfbox.header;

import io.ihankun.framework.pdf.pdfbox.component.XEasyPdfComponent;
import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfDocument;
import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfPage;
import io.ihankun.framework.pdf.pdfbox.component.line.XEasyPdfLine;

import java.io.Serializable;

/**
 * pdf页眉组件接口
 *
 * @author hankun
 */
public interface XEasyPdfHeader extends Serializable {

    /**
     * 开启上下文重置
     *
     * @return 返回页眉组件
     */
    XEasyPdfHeader enableResetContext();

    /**
     * 添加分割线
     *
     * @param splitLine pdf分割线
     * @return 返回页眉组件
     */
    XEasyPdfHeader addSplitLine(XEasyPdfLine... splitLine);

    /**
     * 添加自定义组件
     *
     * @param component pdf组件
     * @return 返回页眉组件
     */
    XEasyPdfHeader addComponent(XEasyPdfComponent component);

    /**
     * 设置边距（上左右）
     *
     * @param margin 边距
     * @return 返回页眉组件
     */
    XEasyPdfHeader setMargin(float margin);

    /**
     * 设置左边距
     *
     * @param margin 边距
     * @return 返回页眉组件
     */
    XEasyPdfHeader setMarginLeft(float margin);

    /**
     * 设置右边距
     *
     * @param margin 边距
     * @return 返回页眉组件
     */
    XEasyPdfHeader setMarginRight(float margin);

    /**
     * 设置上边距
     *
     * @param margin 边距
     * @return 返回页眉组件
     */
    XEasyPdfHeader setMarginTop(float margin);

    /**
     * 获取页眉高度
     *
     * @param document pdf文档
     * @param page     pdf页面
     * @return 返回页眉高度
     */
    float getHeight(XEasyPdfDocument document, XEasyPdfPage page);

    /**
     * 获取总页码占位符
     *
     * @return 返回总页码占位符
     */
    String getTotalPagePlaceholder();

    /**
     * 获取当前页码占位符
     *
     * @return 返回当前页码占位符
     */
    String getCurrentPagePlaceholder();

    /**
     * 获取文本字体路径
     *
     * @return 返回文本字体路径
     */
    String getTextFontPath();

    /**
     * 检查组件
     *
     * @param component 组件
     * @return 返回布尔值，true为是，false为否
     */
    boolean check(XEasyPdfComponent component);

    /**
     * 绘制
     *
     * @param document pdf文档
     * @param page     pdf页面
     */
    void draw(XEasyPdfDocument document, XEasyPdfPage page);
}
