package io.ihankun.framework.pdf.pdfbox.component.layout;

import lombok.Data;
import io.ihankun.framework.pdf.pdfbox.component.table.XEasyPdfTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hankun
 */
@Data
class XEasyPdfLayoutParam implements Serializable {

    private static final long serialVersionUID = 6269937080892192541L;

    /**
     * 是否包含表格边框
     */
    private Boolean hasTableBorder = Boolean.FALSE;
    /**
     * pdf表格
     */
    private XEasyPdfTable table = new XEasyPdfTable();
    /**
     * 组件列表
     */
    private transient List<XEasyPdfLayoutComponent> components = new ArrayList<>(10);
    /**
     * 宽度
     */
    private Float width;
    /**
     * 高度
     */
    private Float height;
}
