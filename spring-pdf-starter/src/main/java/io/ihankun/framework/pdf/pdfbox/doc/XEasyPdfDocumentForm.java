package io.ihankun.framework.pdf.pdfbox.doc;

import java.io.Serializable;

/**
 * pdf文档表单
 *
 * @author hankun
 */
public class XEasyPdfDocumentForm implements Serializable {

    private static final long serialVersionUID = 347480015461916142L;

    /**
     * pdf文档表单填写器
     */
    private final XEasyPdfDocumentFormFiller formFiller;

    /**
     * 有参构造
     *
     * @param formFiller pdf文档表单填写器
     */
    XEasyPdfDocumentForm(XEasyPdfDocumentFormFiller formFiller) {
        this.formFiller = formFiller;
    }

    /**
     * 创建文本属性
     *
     * @return 返回pdf表单文本属性
     */
    public XEasyPdfDocumentFormTextField createTextField() {
        return new XEasyPdfDocumentFormTextField(this);
    }

    /**
     * 完成操作
     *
     * @return 返回pdf文档表单填写器
     */
    public XEasyPdfDocumentFormFiller finish() {
        return this.formFiller;
    }

    /**
     * 获取pdf文档表单填写器
     *
     * @return 返回pdf文档表单填写器
     */
    XEasyPdfDocumentFormFiller getFormFiller() {
        return this.formFiller;
    }
}
