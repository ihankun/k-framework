package io.ihankun.framework.pdf.fop.doc.component.page;

import io.ihankun.framework.pdf.fop.XEasyPdfTemplateAttributes;
import io.ihankun.framework.pdf.fop.XEasyPdfTemplateTags;
import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;

/**
 * pdf模板-总页码组件
 *
 * @author hankun
 */
public class XEasyPdfTemplateTotalPageNumber implements XEasyPdfTemplateComponent {

    /**
     * 页面id
     */
    private String pageId;

    /**
     * 设置页面id
     *
     * @param pageId 页面id
     * @return 返回总页码组件
     */
    public XEasyPdfTemplateTotalPageNumber setPageId(String pageId) {
        this.pageId = pageId;
        return this;
    }

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
        // 创建totalPageNumber元素
        Element totalPageNumber = document.createElement(XEasyPdfTemplateTags.PAGE_NUMBER_CITATION_LAST);
        // 设置引用id
        totalPageNumber.setAttribute(XEasyPdfTemplateAttributes.REF_ID, Optional.ofNullable(this.pageId).orElseThrow(() -> new IllegalArgumentException("the page id can not bu null")));
        // 添加总页码
        block.appendChild(totalPageNumber);
        // 返回block元素
        return block;
    }
}
