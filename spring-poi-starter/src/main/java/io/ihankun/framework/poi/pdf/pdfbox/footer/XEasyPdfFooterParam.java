package io.ihankun.framework.poi.pdf.pdfbox.footer;

import lombok.Data;
import lombok.experimental.Accessors;
import io.ihankun.framework.poi.pdf.pdfbox.component.XEasyPdfComponent;
import io.ihankun.framework.poi.pdf.pdfbox.component.image.XEasyPdfImage;
import io.ihankun.framework.poi.pdf.pdfbox.component.text.XEasyPdfText;
import io.ihankun.framework.poi.pdf.pdfbox.doc.XEasyPdfDocument;
import io.ihankun.framework.poi.pdf.pdfbox.doc.XEasyPdfPage;

import java.io.Serializable;

/**
 * pdf页脚组件参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
class XEasyPdfFooterParam implements Serializable {

    private static final long serialVersionUID = -1024397509263922127L;

    /**
     * 是否重置上下文
     */
    private Boolean isResetContext;
    /**
     * 文本
     */
    private XEasyPdfText text;
    /**
     * 图片
     */
    private XEasyPdfImage image;
    /**
     * 自定义组件
     */
    private XEasyPdfComponent component;
    /**
     * 高度
     */
    private Float height;
    /**
     * 左边距
     */
    private Float marginLeft = 0F;
    /**
     * 右边距
     */
    private Float marginRight = 0F;
    /**
     * 下边距
     */
    private Float marginBottom = 5F;
    /**
     * 页面X轴起始坐标
     */
    private Float beginX;
    /**
     * 页面Y轴起始坐标
     */
    private Float beginY;

    /**
     * 初始化
     *
     * @param document pdf文档
     * @param page     pdf页面
     */
    void init(XEasyPdfDocument document, XEasyPdfPage page) {
        // 如果重置上下文未初始化，则初始化为文档重置上下文
        if (this.isResetContext == null) {
            // 初始化为文档重置上下文
            this.isResetContext = page.isResetContext();
        }
        // 初始化高度
        this.initHeight(document, page);
        // 初始化X轴起始坐标
        this.beginX = this.marginLeft;
        // 初始化Y轴起始坐标
        this.beginY = 0F;
    }

    /**
     * 初始化高度
     *
     * @param document pdf文档
     * @param page     pdf页面
     */
    private void initHeight(XEasyPdfDocument document, XEasyPdfPage page) {
        // 定义文本高度
        float textHeight = 0F;
        // 如果文本不为空，则获取文本高度
        if (this.text != null) {
            // 获取文本高度
            textHeight = this.text.getHeight(document, page);
        }
        // 定义图片高度
        float imageHeight = 0F;
        // 如果图片不为空，则获取图片高度
        if (this.image != null) {
            // 关闭图片自适应
            this.image.disableSelfAdaption();
            // 如果自定义图片宽度为空，则设置为页面宽度
            if (this.image.getWidth(document, page) == null) {
                // 设置为页面宽度
                this.image.setWidth(page.getLastPage().getMediaBox().getWidth());
            }
            // 获取图片高度
            imageHeight = this.image.getHeight(document, page);
        }
        // 如果高度未初始化，则进行初始化
        if (this.height == null) {
            // 初始化高度，文本高度与图片高度取最大值，加下边距
            this.height = Math.max(textHeight, imageHeight) + this.marginBottom;
        }
    }
}
