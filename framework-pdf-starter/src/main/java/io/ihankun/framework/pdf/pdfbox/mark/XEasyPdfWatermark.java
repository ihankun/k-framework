package io.ihankun.framework.pdf.pdfbox.mark;


import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfDocument;
import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfPage;

import java.io.Serializable;

/**
 * pdf水印组件接口
 *
 * @author hankun
 */
public interface XEasyPdfWatermark extends Serializable {

    /**
     * 开启上下文重置
     *
     * @return 返回水印组件
     */
    XEasyPdfWatermark enableResetContext();

    /**
     * 绘制
     *
     * @param document pdf文档
     * @param page     pdf页面
     */
    void draw(XEasyPdfDocument document, XEasyPdfPage page);
}
