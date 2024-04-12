package io.ihankun.framework.pdf.fop.handler;

import io.ihankun.framework.pdf.fop.XEasyPdfTemplateAttributes;
import org.w3c.dom.Element;

import java.awt.*;

/**
 * pdf模板元素助手
 *
 * @author hankun
 */
public class XEasyPdfTemplateElementHandler {

    /**
     * 添加子元素
     *
     * @param parent 父元素
     * @param child  子元素
     */
    public static void appendChild(Element parent, Element child) {
        if (parent != null && child != null) {
            parent.appendChild(child);
        }
    }

    /**
     * 添加颜色
     *
     * @param element 元素
     * @param color   颜色
     */
    public static void appendColor(Element element, Color color) {
        appendColor(element, XEasyPdfTemplateAttributes.COLOR, color);
    }

    /**
     * 添加颜色
     *
     * @param element   元素
     * @param attribute 属性
     * @param color     颜色
     */
    public static void appendColor(Element element, String attribute, Color color) {
        if (element != null && color != null) {
            element.setAttribute(
                    attribute,
                    String.join(
                            "",
                            "rgb(",
                            String.valueOf(color.getRed()),
                            ",",
                            String.valueOf(color.getGreen()),
                            ",",
                            String.valueOf(color.getBlue()),
                            ")"
                    )
            );
        }
    }
}
