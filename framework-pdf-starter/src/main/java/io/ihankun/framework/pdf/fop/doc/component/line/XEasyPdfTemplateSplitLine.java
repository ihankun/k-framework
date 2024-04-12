package io.ihankun.framework.pdf.fop.doc.component.line;

import io.ihankun.framework.pdf.fop.XEasyPdfTemplateAttributes;
import io.ihankun.framework.pdf.fop.XEasyPdfTemplateConstants;
import io.ihankun.framework.pdf.fop.XEasyPdfTemplateTags;
import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;

/**
 * pdf模板-分割线组件
 * <p>fo:leader</p>
 *
 * @author hankun
 */
public class XEasyPdfTemplateSplitLine implements XEasyPdfTemplateComponent {

    /**
     * 分割线参数
     */
    private final XEasyPdfTemplateSplitLineParam param = new XEasyPdfTemplateSplitLineParam();

    /**
     * 设置上下左右边距
     *
     * @param margin 边距
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setMargin(String margin) {
        this.param.setMargin(margin);
        return this;
    }

    /**
     * 设置上边距
     *
     * @param marginTop 上边距
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setMarginTop(String marginTop) {
        this.param.setMarginTop(marginTop);
        return this;
    }

    /**
     * 设置下边距
     *
     * @param marginBottom 下边距
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setMarginBottom(String marginBottom) {
        this.param.setMarginBottom(marginBottom);
        return this;
    }

    /**
     * 设置左边距
     *
     * @param marginLeft 左边距
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setMarginLeft(String marginLeft) {
        this.param.setMarginLeft(marginLeft);
        return this;
    }

    /**
     * 设置右边距
     *
     * @param paddingRight 右边距
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setMarginRight(String paddingRight) {
        this.param.setMarginRight(paddingRight);
        return this;
    }

    /**
     * 设置上下左右填充
     *
     * @param padding 填充
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setPadding(String padding) {
        this.param.setPadding(padding);
        return this;
    }

    /**
     * 设置上填充
     *
     * @param paddingTop 上填充
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setPaddingTop(String paddingTop) {
        this.param.setPaddingTop(paddingTop);
        return this;
    }

    /**
     * 设置下填充
     *
     * @param paddingBottom 下填充
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setPaddingBottom(String paddingBottom) {
        this.param.setPaddingBottom(paddingBottom);
        return this;
    }

    /**
     * 设置左填充
     *
     * @param paddingLeft 左填充
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setPaddingLeft(String paddingLeft) {
        this.param.setPaddingLeft(paddingLeft);
        return this;
    }

    /**
     * 设置右填充
     *
     * @param paddingRight 右填充
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setPaddingRight(String paddingRight) {
        this.param.setPaddingRight(paddingRight);
        return this;
    }

    /**
     * 设置id
     *
     * @param id id
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setId(String id) {
        this.param.setId(id);
        return this;
    }

    /**
     * 设置长度
     *
     * @param length 长度
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setLength(String length) {
        this.param.setLength(length);
        return this;
    }

    /**
     * 设置样式
     * <p>none：无</p>
     * <p>dotted：点线</p>
     * <p>dashed：虚线</p>
     * <p>solid：实线</p>
     * <p>double：双实线</p>
     * <p>groove：槽线</p>
     * <p>ridge：脊线</p>
     *
     * @param style 样式
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setStyle(String style) {
        this.param.setStyle(style);
        return this;
    }

    /**
     * 设置颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     *
     * @param color 颜色
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setColor(String color) {
        this.param.setColor(color);
        return this;
    }

    /**
     * 设置文本水平样式
     * <p>left：居左</p>
     * <p>center：居中</p>
     * <p>right：居右</p>
     *
     * @param style 水平样式
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setHorizontalStyle(String style) {
        this.param.setHorizontalStyle(style);
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
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setBreakBefore(String breakBefore) {
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
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine setBreakAfter(String breakAfter) {
        this.param.setBreakAfter(breakAfter);
        return this;
    }

    /**
     * 开启分页时保持
     *
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine enableKeepTogether() {
        this.param.setKeepTogether("always");
        return this;
    }

    /**
     * 开启分页时与上一个元素保持
     *
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine enableKeepWithPrevious() {
        this.param.setKeepWithPrevious("always");
        return this;
    }

    /**
     * 开启分页时与下一个元素保持
     *
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine enableKeepWithNext() {
        this.param.setKeepWithNext("always");
        return this;
    }

    /**
     * 开启边框（调试时使用）
     *
     * @return 返回分割线组件
     */
    public XEasyPdfTemplateSplitLine enableBorder() {
        this.param.setHasBorder(Boolean.TRUE);
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
        // 添加leader元素
        block.appendChild(this.createLeader(document));
        // 返回block元素
        return block;
    }

    /**
     * 创建leader元素
     *
     * @param document fo文档
     * @return 返回leader元素
     */
    public Element createLeader(Document document) {
        // 创建leader元素
        Element leader = document.createElement(XEasyPdfTemplateTags.LEADER);
        // 设置分割线长度
        Optional.ofNullable(this.param.getLength()).ifPresent(
                v -> leader.setAttribute(XEasyPdfTemplateAttributes.LEADER_LENGTH, v.intern().toLowerCase())
        );
        // 设置分割线样式（默认：solid）
        Optional.ofNullable(this.param.getStyle()).ifPresent(
                v -> {
                    // 如果分割线样式为虚线，则设置为虚线样式
                    if ("dotted".equalsIgnoreCase(v)) {
                        // 设置虚线样式
                        leader.setAttribute(XEasyPdfTemplateAttributes.LEADER_PATTERN, XEasyPdfTemplateConstants.DEFAULT_DOTTED_SPLIT_LINE_STYLE_VALUE);
                    }
                    // 否则设置为规则样式
                    else {
                        // 设置分割线样式
                        leader.setAttribute(XEasyPdfTemplateAttributes.LEADER_PATTERN, XEasyPdfTemplateConstants.DEFAULT_SPLIT_LINE_STYLE_VALUE);
                        // 设置规则样式
                        leader.setAttribute(XEasyPdfTemplateAttributes.RULE_STYLE, v.intern().toLowerCase());
                    }
                }
        );
        // 设置分割线颜色
        Optional.ofNullable(this.param.getColor()).ifPresent(v -> leader.setAttribute(XEasyPdfTemplateAttributes.COLOR, v.intern().toLowerCase()));
        // 返回leader元素
        return leader;
    }
}
