package io.ihankun.framework.pdf.fop.doc.component.link;

import io.ihankun.framework.pdf.fop.XEasyPdfTemplateAttributes;
import io.ihankun.framework.pdf.fop.XEasyPdfTemplateTags;
import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponent;
import io.ihankun.framework.pdf.fop.doc.component.text.XEasyPdfTemplateText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;

/**
 * pdf模板-超链接组件
 * <p>fo:basic-link</p>
 *
 * @author hankun
 */
public class XEasyPdfTemplateLink implements XEasyPdfTemplateComponent {

    /**
     * 超链接参数
     */
    private final XEasyPdfTemplateLinkParam param = new XEasyPdfTemplateLinkParam();

    /**
     * 设置id
     *
     * @param id id
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink setId(String id) {
        this.param.setId(id);
        return this;
    }

    /**
     * 设置文本
     *
     * @param text 文本组件
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink setText(XEasyPdfTemplateText text) {
        this.param.setComponent(text);
        return this;
    }

    /**
     * 设置内部地址
     * <p>注：标签id</p>
     *
     * @param destination 地址
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink setInternalDestination(String destination) {
        this.param.setInternalDestination(destination);
        return this;
    }

    /**
     * 设置外部地址
     * <p>注：url</p>
     *
     * @param destination 地址
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink setExternalDestination(String destination) {
        this.param.setExternalDestination(destination);
        return this;
    }

    /**
     * 设置分页符-前
     * <p>auto：自动</p>
     * <p>column：分列</p>
     * <p>page：分页</p>
     * <p>even-page：在元素之前强制分页一次或两个，以便下一页将成为偶数页</p>
     * <p>odd-page：在元素之前强制分页一次或两个，以便下一页将成为奇数页</p>
     *
     * @param breakBefore 分页符
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink setBreakBefore(String breakBefore) {
        this.param.setBreakBefore(breakBefore);
        return this;
    }

    /**
     * 设置分页符-后
     * <p>auto：自动</p>
     * <p>column：分列</p>
     * <p>page：分页</p>
     * <p>even-page：在元素之后强制分页一次或两个，以便下一页将成为偶数页</p>
     * <p>odd-page：在元素之后强制分页一次或两个，以便下一页将成为奇数页</p>
     *
     * @param breakAfter 分页符
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink setBreakAfter(String breakAfter) {
        this.param.setBreakAfter(breakAfter);
        return this;
    }

    /**
     * 开启分页时保持
     *
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink enableKeepTogether() {
        this.param.setKeepTogether("always");
        return this;
    }

    /**
     * 开启分页时与上一个元素保持
     *
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink enableKeepWithPrevious() {
        this.param.setKeepWithPrevious("always");
        return this;
    }

    /**
     * 开启分页时与下一个元素保持
     *
     * @return 返回超链接组件
     */
    public XEasyPdfTemplateLink enableKeepWithNext() {
        this.param.setKeepWithNext("always");
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
        Element block = this.createBlockElement(document, this.param);
        // 添加link元素
        block.appendChild(this.createLink(document));
        // 返回block元素
        return block;
    }

    /**
     * 创建link元素
     *
     * @param document fo文档
     * @return 返回link元素
     */
    private Element createLink(Document document) {
        // 创建link元素
        Element link = document.createElement(XEasyPdfTemplateTags.BASIC_LINK);
        // 设置内部地址
        Optional.ofNullable(this.param.getInternalDestination()).ifPresent(v -> link.setAttribute(XEasyPdfTemplateAttributes.INTERNAL_DESTINATION, v.intern()));
        // 设置外部地址
        Optional.ofNullable(this.param.getExternalDestination()).ifPresent(v -> link.setAttribute(XEasyPdfTemplateAttributes.EXTERNAL_DESTINATION, v.intern()));
        // 添加组件
        Optional.ofNullable(this.param.getComponent()).ifPresent(v -> link.appendChild(v.createElement(document)));
        // 返回link元素
        return link;
    }
}
