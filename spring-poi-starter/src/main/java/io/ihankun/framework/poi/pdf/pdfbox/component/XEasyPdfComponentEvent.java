package io.ihankun.framework.poi.pdf.pdfbox.component;

import io.ihankun.framework.poi.pdf.pdfbox.doc.XEasyPdfDocument;
import io.ihankun.framework.poi.pdf.pdfbox.doc.XEasyPdfPage;

/**
 * pdf组件事件
 *
 * @author hankun
 */
public interface XEasyPdfComponentEvent {

    /**
     * 执行
     *
     * @param document  pdf文档
     * @param page      pdf页面
     * @param component pdf组件
     */
    void execute(XEasyPdfDocument document, XEasyPdfPage page, XEasyPdfComponent component);
}
