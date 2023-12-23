package io.ihankun.framework.poi.pdf.fop.doc.component.page;

import io.ihankun.framework.poi.pdf.fop.XEasyPdfTemplateTags;
import io.ihankun.framework.poi.pdf.fop.doc.component.XEasyPdfTemplateComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * pdf模板-当前页码组件
 *
 * @author hankun
 */
public class XEasyPdfTemplateCurrentPageNumber implements XEasyPdfTemplateComponent {

    /**
     * 创建元素
     *
     * @param document fo文档
     * @return 返回元素
     */
    @Override
    public Element createElement(Document document) {
        // 创建block元素
        Element block = this.createEmptyElement(document);
        // 添加当前页码
        block.appendChild(document.createElement(XEasyPdfTemplateTags.PAGE_NUMBER));
        // 返回block元素
        return block;
    }
}
