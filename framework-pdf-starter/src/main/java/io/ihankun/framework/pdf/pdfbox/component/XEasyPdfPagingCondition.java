package io.ihankun.framework.pdf.pdfbox.component;

import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfDocument;
import io.ihankun.framework.pdf.pdfbox.doc.XEasyPdfPage;

/**
 * pdf分页条件
 *
 * @author hankun
 */
public interface XEasyPdfPagingCondition {

    /**
     * 是否分页
     *
     * @param document   pdf文档
     * @param page       pdf页面
     * @param componentY 当前组件Y轴坐标
     * @return 返回布尔值，是为true，否为false
     */
    boolean isPaging(XEasyPdfDocument document, XEasyPdfPage page, Float componentY);
}
