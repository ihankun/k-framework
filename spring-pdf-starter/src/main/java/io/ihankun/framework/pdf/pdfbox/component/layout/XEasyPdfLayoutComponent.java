package io.ihankun.framework.pdf.pdfbox.component.layout;

import io.ihankun.framework.pdf.pdfbox.component.XEasyPdfComponent;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * pdf布局组件
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
public class XEasyPdfLayoutComponent {
    /**
     * pdf组件
     */
    private XEasyPdfComponent component;
    /**
     * 宽度
     */
    private Float width;
    /**
     * 高度
     */
    private Float height;

    /**
     * 有参构造
     *
     * @param width  宽度
     * @param height 高度
     */
    public XEasyPdfLayoutComponent(Float width, Float height) {
        this.width = width;
        this.height = height;
    }
}
