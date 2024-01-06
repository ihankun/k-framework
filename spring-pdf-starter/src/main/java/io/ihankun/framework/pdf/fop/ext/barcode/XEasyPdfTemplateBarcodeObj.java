package io.ihankun.framework.pdf.fop.ext.barcode;

import io.ihankun.framework.pdf.fop.XEasyPdfTemplateConstants;
import org.apache.fop.fo.FONode;
import org.apache.fop.fo.XMLObj;

/**
 * 条形码对象
 *
 * @author hankun
 */
public class XEasyPdfTemplateBarcodeObj extends XMLObj {

    /**
     * 有参构造
     *
     * @param parent 父节点
     */
    public XEasyPdfTemplateBarcodeObj(FONode parent) {
        super(parent);
    }

    /**
     * 获取命名空间
     *
     * @return 返回命名空间
     */
    public String getNamespaceURI() {
        return XEasyPdfTemplateConstants.NAMESPACE;
    }

    /**
     * 获取命名空间前缀
     *
     * @return 返回命名空间前缀
     */
    public String getNormalNamespacePrefix() {
        return "xe";
    }
}
