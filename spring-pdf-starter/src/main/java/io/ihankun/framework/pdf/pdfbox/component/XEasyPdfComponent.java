package io.ihankun.framework.pdf.pdfbox.component;


import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfDocument;
import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.Serializable;

/**
 * pdf组件接口
 *
 * @author hankun
 */
public interface XEasyPdfComponent extends Serializable {

    /**
     * 设置坐标
     *
     * @param beginX X轴起始坐标
     * @param beginY Y轴起始坐标
     * @return 返回pdf组件
     */
    XEasyPdfComponent setPosition(float beginX, float beginY);

    /**
     * 设置宽度
     *
     * @param width 宽度
     * @return 返回pdf组件
     */
    XEasyPdfComponent setWidth(float width);

    /**
     * 设置高度
     *
     * @param height 高度
     * @return 返回pdf组件
     */
    XEasyPdfComponent setHeight(float height);

    /**
     * 设置内容模式
     *
     * @param mode 内容模式
     * @return 返回pdf组件
     */
    XEasyPdfComponent setContentMode(ContentMode mode);

    /**
     * 开启上下文重置
     *
     * @return 返回pdf组件
     */
    XEasyPdfComponent enableResetContext();

    /**
     * 绘制
     *
     * @param document pdf文档
     * @param page     pdf页面
     */
    void draw(XEasyPdfDocument document, XEasyPdfPage page);

    /**
     * 内容模式
     */
    enum ContentMode {
        /**
         * 覆盖
         */
        OVERWRITE(PDPageContentStream.AppendMode.OVERWRITE),
        /**
         * 追加
         */
        APPEND(PDPageContentStream.AppendMode.APPEND),
        /**
         * 前置
         */
        PREPEND(PDPageContentStream.AppendMode.PREPEND);

        /**
         * pdfbox追加模式
         */
        private final PDPageContentStream.AppendMode appendMode;

        /**
         * 构造方法
         *
         * @param appendMode pdfbox追加模式
         */
        ContentMode(PDPageContentStream.AppendMode appendMode) {
            this.appendMode = appendMode;
        }

        /**
         * 获取追加模式
         *
         * @return 返回pdfbox追加模式
         */
        public PDPageContentStream.AppendMode getMode() {
            return this.appendMode;
        }
    }
}
