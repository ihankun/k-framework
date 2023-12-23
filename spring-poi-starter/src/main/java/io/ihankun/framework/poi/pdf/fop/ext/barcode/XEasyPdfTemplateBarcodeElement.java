package io.ihankun.framework.poi.pdf.fop.ext.barcode;

import lombok.SneakyThrows;
import org.apache.fop.fo.FONode;
import org.apache.fop.fo.PropertyList;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import java.awt.geom.Point2D;

/**
 * 条形码元素
 *
 * @author hankun
 */
public class XEasyPdfTemplateBarcodeElement extends XEasyPdfTemplateBarcodeObj {

    /**
     * 有参构造
     *
     * @param parent 父节点
     */
    public XEasyPdfTemplateBarcodeElement(FONode parent) {
        super(parent);
    }

    /**
     * 处理节点
     *
     * @param elementName  元素名称
     * @param locator      定位器
     * @param attlist      当前节点属性列表
     * @param propertyList 父节点属性列表
     */
    @SneakyThrows
    public void processNode(String elementName, Locator locator, Attributes attlist, PropertyList propertyList) {
        super.processNode(elementName, locator, attlist, propertyList);
        this.createBasicDocument();
    }

    /**
     * 获取维度
     *
     * @param view 2d维度
     * @return 返回维度
     */
    public Point2D getDimension(Point2D view) {
        Node barcode = this.doc.getFirstChild();
        if (barcode != null && XEasyPdfTemplateBarcodeImageHandler.IMAGE_TYPE.equals(barcode.getLocalName())) {
            XEasyPdfTemplateBarCodeConfig config = XEasyPdfTemplateBarCodeConfig.onlyInitRectangle(barcode.getAttributes());
            return new Point2D.Float((float) config.getWidth(), (float) config.getHeight());
        }
        return null;
    }
}
