package io.ihankun.framework.pdf.pdfbox.component.layout;

import io.ihankun.framework.pdf.pdfbox.component.XEasyPdfComponent;

import java.util.List;

/**
 * pdf布局组件
 *
 * @author hankun
 */
public interface XEasyPdfLayout extends XEasyPdfComponent {

    /**
     * 开启边框
     *
     * @return 返回pdf布局
     */
    XEasyPdfLayout enableBorder();

    /**
     * 添加组件
     *
     * @param components 组件列表
     * @return 返回pdf布局
     */
    XEasyPdfLayout addLayoutComponent(XEasyPdfLayoutComponent... components);

    /**
     * 添加组件
     *
     * @param components 组件列表
     * @return 返回pdf布局
     */
    XEasyPdfLayout addLayoutComponent(List<XEasyPdfLayoutComponent> components);
}
