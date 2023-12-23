package io.ihankun.framework.poi.pdf.fop.doc.page;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * pdf模板-页面组件
 *
 * @author hankun
 */
public interface XEasyPdfTemplatePageComponent {

    /**
     * 创建元素
     *
     * @param index    当前索引
     * @param document fo文档
     * @param bookmark 书签元素
     * @return 返回节点
     */
    Element createElement(int index, Document document, Element bookmark);
}
