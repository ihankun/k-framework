package io.ihankun.framework.pdf.fop.doc.bookmark;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * pdf模板-书签组件
 *
 * @author hankun
 */
public interface XEasyPdfTemplateBookmarkComponent {

    /**
     * 创建元素
     *
     * @param document fo文档
     * @return 返回元素
     */
    Element createElement(Document document);
}
