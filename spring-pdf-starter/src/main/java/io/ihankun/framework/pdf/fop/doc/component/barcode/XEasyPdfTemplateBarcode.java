package io.ihankun.framework.pdf.fop.doc.component.barcode;

import io.ihankun.framework.pdf.fop.XEasyPdfTemplateAttributes;
import io.ihankun.framework.pdf.fop.XEasyPdfTemplateTags;
import io.ihankun.framework.pdf.fop.doc.component.XEasyPdfTemplateComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;

/**
 * pdf模板-条形码组件
 * <p>fo:barcode</p>
 *
 * @author hankun
 */
public class XEasyPdfTemplateBarcode implements XEasyPdfTemplateComponent {

    /**
     * 条形码参数
     */
    private final XEasyPdfTemplateBarcodeParam param = new XEasyPdfTemplateBarcodeParam();

    /**
     * 设置上下左右边距
     *
     * @param margin 边距
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setMargin(String margin) {
        this.param.setMargin(margin);
        return this;
    }

    /**
     * 设置上边距
     *
     * @param marginTop 上边距
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setMarginTop(String marginTop) {
        this.param.setMarginTop(marginTop);
        return this;
    }

    /**
     * 设置下边距
     *
     * @param marginBottom 下边距
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setMarginBottom(String marginBottom) {
        this.param.setMarginBottom(marginBottom);
        return this;
    }

    /**
     * 设置左边距
     *
     * @param marginLeft 左边距
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setMarginLeft(String marginLeft) {
        this.param.setMarginLeft(marginLeft);
        return this;
    }

    /**
     * 设置右边距
     *
     * @param paddingRight 右边距
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setMarginRight(String paddingRight) {
        this.param.setMarginRight(paddingRight);
        return this;
    }

    /**
     * 设置上下左右填充
     *
     * @param padding 填充
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setPadding(String padding) {
        this.param.setPadding(padding);
        return this;
    }

    /**
     * 设置上填充
     *
     * @param paddingTop 上填充
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setPaddingTop(String paddingTop) {
        this.param.setPaddingTop(paddingTop);
        return this;
    }

    /**
     * 设置下填充
     *
     * @param paddingBottom 下填充
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setPaddingBottom(String paddingBottom) {
        this.param.setPaddingBottom(paddingBottom);
        return this;
    }

    /**
     * 设置左填充
     *
     * @param paddingLeft 左填充
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setPaddingLeft(String paddingLeft) {
        this.param.setPaddingLeft(paddingLeft);
        return this;
    }

    /**
     * 设置右填充
     *
     * @param paddingRight 右填充
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setPaddingRight(String paddingRight) {
        this.param.setPaddingRight(paddingRight);
        return this;
    }

    /**
     * 设置id
     *
     * @param id id
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setId(String id) {
        this.param.setId(id);
        return this;
    }

    /**
     * 设置条形码类型
     * <p>一维码：</p>
     * <p>codabar：库德巴码</p>
     * <p>code_39：标准39码</p>
     * <p>code_93：标准93码</p>
     * <p>code_128：标准128码</p>
     * <p>ean_8：缩短版国际商品条码</p>
     * <p>ean_13：标准版国际商品条码</p>
     * <p>itf：交叉码</p>
     * <p>upc_a：美国商品码a</p>
     * <p>upc_e：美国商品码e</p>
     * <p>upc_ean_extension：美国商品码扩展码</p>
     * <p>二维码：</p>
     * <p>qr_code：qr码</p>
     * <p>aztec：阿兹特克码</p>
     * <p>data_matrix：dm码</p>
     * <p>maxi_code：maxi码</p>
     * <p>pdf_417：pdf-417码</p>
     * <p>rss_14：rss-14码</p>
     * <p>rss_expanded：rss扩展码</p>
     *
     * @param type 类型
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setType(String type) {
        this.param.setType(type);
        return this;
    }

    /**
     * 设置条形码缩放比例（实际比例）
     *
     * @param scaleRate 缩放比例
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setScaleRate(String scaleRate) {
        this.param.setScaleRate(scaleRate);
        return this;
    }

    /**
     * 设置条形码旋转弧度
     *
     * @param radians 旋转弧度
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setRadians(String radians) {
        this.param.setRadians(radians);
        return this;
    }

    /**
     * 设置条形码边距
     *
     * @param codeMargin 条形码边距
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setCodeMargin(String codeMargin) {
        this.param.setCodeMargin(codeMargin);
        return this;
    }

    /**
     * 设置条形码纠错级别
     *
     * @param errorLevel 纠错级别
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setErrorLevel(String errorLevel) {
        this.param.setErrorLevel(errorLevel);
        return this;
    }

    /**
     * 设置条形码图像宽度
     *
     * @param width 图像宽度
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setWidth(String width) {
        this.param.setWidth(width);
        return this;
    }

    /**
     * 设置条形码图像高度
     *
     * @param height 图像高度
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setHeight(String height) {
        this.param.setHeight(height);
        return this;
    }

    /**
     * 设置条形码内容
     *
     * @param content 内容
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setContent(String content) {
        this.param.setContent(content);
        return this;
    }

    /**
     * 设置条形码前景颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     *
     * @param onColor 前景颜色
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setOnColor(String onColor) {
        this.param.setOnColor(onColor);
        return this;
    }

    /**
     * 设置条形码背景颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     *
     * @param offColor 背景颜色
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setOffColor(String offColor) {
        this.param.setOffColor(offColor);
        return this;
    }

    /**
     * 设置条形码文字
     *
     * @param words 文字
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setWords(String words) {
        this.param.setWords(words);
        return this;
    }

    /**
     * 设置条形码文字颜色
     * <p>color：颜色（名称或16进制颜色）</p>
     * <p>transparent：透明</p>
     *
     * @param wordsColor 文字颜色
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setWordsColor(String wordsColor) {
        this.param.setWordsColor(wordsColor);
        return this;
    }

    /**
     * 设置条形码文字名称
     *
     * @param wordsFamily 文字名称
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setWordsFamily(String wordsFamily) {
        this.param.setWordsFamily(wordsFamily);
        return this;
    }

    /**
     * 设置条形码文字样式
     * <p>normal：正常</p>
     * <p>bold：粗体</p>
     * <p>bold_italic：粗体斜体</p>
     * <p>italic：斜体</p>
     *
     * @param wordsStyle 文字样式
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setWordsStyle(String wordsStyle) {
        this.param.setWordsStyle(wordsStyle);
        return this;
    }

    /**
     * 设置条形码文字大小
     *
     * @param wordsSize 文字大小
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setWordsSize(String wordsSize) {
        this.param.setWordsSize(wordsSize);
        return this;
    }

    /**
     * 设置条形码文字偏移量-X轴
     *
     * @param wordsOffsetX 文字偏移量-X轴
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setWordsOffsetX(String wordsOffsetX) {
        this.param.setWordsOffsetX(wordsOffsetX);
        return this;
    }

    /**
     * 设置条形码文字偏移量-Y轴
     *
     * @param wordsOffsetY 文字偏移量-Y轴
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setWordsOffsetY(String wordsOffsetY) {
        this.param.setWordsOffsetY(wordsOffsetY);
        return this;
    }

    /**
     * 设置文本水平样式
     * <p>left：居左</p>
     * <p>center：居中</p>
     * <p>right：居右</p>
     *
     * @param style 水平样式
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setHorizontalStyle(String style) {
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
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setBreakBefore(String breakBefore) {
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
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode setBreakAfter(String breakAfter) {
        this.param.setBreakAfter(breakAfter);
        return this;
    }

    /**
     * 开启分页时保持
     *
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode enableKeepTogether() {
        this.param.setKeepTogether("always");
        return this;
    }

    /**
     * 开启分页时与上一个元素保持
     *
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode enableKeepWithPrevious() {
        this.param.setKeepWithPrevious("always");
        return this;
    }

    /**
     * 开启分页时与下一个元素保持
     *
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode enableKeepWithNext() {
        this.param.setKeepWithNext("always");
        return this;
    }

    /**
     * 开启边框（调试时使用）
     *
     * @return 返回条形码组件
     */
    public XEasyPdfTemplateBarcode enableBorder() {
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
        // 如果条形码类型为空，则返回空元素
        if (this.param.getType() == null) {
            // 返回空元素
            return null;
        }
        // 如果条形码内容为空，则返回空元素
        if (this.param.getContent() == null) {
            // 返回空元素
            return null;
        }
        // 创建block元素
        Element block = this.createBlockElement(document, this.param);
        // 添加foreignObject元素
        block.appendChild(this.createForeignObject(document));
        // 返回block元素
        return block;
    }

    /**
     * 创建foreignObject元素
     *
     * @param document fo文档
     * @return 返回元素
     */
    private Element createForeignObject(Document document) {
        // 创建foreignObject元素
        Element foreignObject = document.createElement(XEasyPdfTemplateTags.INSTREAM_FOREIGN_OBJECT);
        // 创建barcode元素
        Element barcode = document.createElement(XEasyPdfTemplateTags.BARCODE);
        // 设置条形码类型
        Optional.ofNullable(this.param.getType()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.TYPE, v.intern().toLowerCase()));
        // 设置条形码缩放比例
        Optional.ofNullable(this.param.getScaleRate()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.SCALE_RATE, v.intern().toLowerCase()));
        // 设置条形码旋转弧度
        Optional.ofNullable(this.param.getRadians()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.RADIANS, v.intern().toLowerCase()));
        // 设置条形码边距
        Optional.ofNullable(this.param.getCodeMargin()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.CODE_MARGIN, v.intern().toLowerCase()));
        // 设置条形码纠错级别
        Optional.ofNullable(this.param.getErrorLevel()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.ERROR_LEVEL, v.intern().toLowerCase()));
        // 设置条形码图像宽度
        Optional.ofNullable(this.param.getWidth()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.WIDTH, v.intern().toLowerCase()));
        // 设置条形码图像高度
        Optional.ofNullable(this.param.getHeight()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.HEIGHT, v.intern().toLowerCase()));
        // 设置条形码内容
        Optional.ofNullable(this.param.getContent()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.CONTENT, v.intern()));
        // 设置条形码前景颜色
        Optional.ofNullable(this.param.getOnColor()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.ON_COLOR, v.intern().toLowerCase()));
        // 设置条形码背景颜色
        Optional.ofNullable(this.param.getOffColor()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.OFF_COLOR, v.intern().toLowerCase()));
        // 设置条形码文字
        Optional.ofNullable(this.param.getWords()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.WORDS, v.intern()));
        // 设置条形码文字颜色
        Optional.ofNullable(this.param.getWordsColor()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.WORDS_COLOR, v.intern().toLowerCase()));
        // 设置条形码文字名称
        Optional.ofNullable(this.param.getWordsFamily()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.WORDS_FAMILY, v.intern().toLowerCase()));
        // 设置条形码文字样式
        Optional.ofNullable(this.param.getWordsStyle()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.WORDS_STYLE, v.intern().toLowerCase()));
        // 设置条形码文字大小
        Optional.ofNullable(this.param.getWordsSize()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.WORDS_SIZE, v.intern().toLowerCase()));
        // 设置条形码文字偏移量-X轴
        Optional.ofNullable(this.param.getWordsOffsetX()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.WORDS_OFFSET_X, v.intern().toLowerCase()));
        // 设置条形码文字偏移量-Y轴
        Optional.ofNullable(this.param.getWordsOffsetY()).ifPresent(v -> barcode.setAttribute(XEasyPdfTemplateAttributes.WORDS_OFFSET_Y, v.intern().toLowerCase()));
        // 添加barcode元素
        foreignObject.appendChild(barcode);
        // 返回foreignObject元素
        return foreignObject;
    }
}
