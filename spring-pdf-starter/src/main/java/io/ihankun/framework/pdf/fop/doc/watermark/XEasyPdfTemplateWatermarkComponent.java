package io.ihankun.framework.pdf.fop.doc.watermark;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * pdf模板-水印组件
 *
 * @author hankun
 */
public interface XEasyPdfTemplateWatermarkComponent {

    /**
     * 创建水印
     *
     * @param document fo文档
     * @param element  fo元素
     */
    void createWatermark(Document document, Element element);
}
