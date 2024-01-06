package io.ihankun.framework.pdf.fop.ext.barcode;

import io.ihankun.framework.pdf.fop.XEasyPdfTemplateConstants;
import org.apache.fop.fo.ElementMapping;
import org.apache.fop.fo.FONode;
import org.w3c.dom.DOMImplementation;

import java.util.HashMap;

/**
 * 条形码元素映射
 *
 * @author hankun
 */
public class XEasyPdfTemplateBarcodeElementMapping extends ElementMapping {

    /**
     * 无参构造
     */
    public XEasyPdfTemplateBarcodeElementMapping() {
        this.namespaceURI = XEasyPdfTemplateConstants.NAMESPACE;
        this.initialize();
    }

    /**
     * 获取文档实现
     *
     * @return 返回文档实现
     */
    public DOMImplementation getDOMImplementation() {
        return getDefaultDOMImplementation();
    }

    /**
     * 初始化
     */
    protected void initialize() {
        if (this.foObjs == null) {
            this.foObjs = new HashMap<>(1);
            this.foObjs.put(XEasyPdfTemplateBarcodeImageHandler.IMAGE_TYPE, new BarcodeMaker());
        }
    }

    /**
     * 条形码生成器
     */
    static class BarcodeMaker extends Maker {
        public FONode make(FONode parent) {
            return new XEasyPdfTemplateBarcodeElement(parent);
        }
    }
}
