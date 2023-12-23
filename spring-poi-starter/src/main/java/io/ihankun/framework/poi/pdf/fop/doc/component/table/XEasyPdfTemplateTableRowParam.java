package io.ihankun.framework.poi.pdf.fop.doc.component.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * pdf模板-表格行参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
class XEasyPdfTemplateTableRowParam extends XEasyPdfTemplateTableParam {

    /**
     * 单元格列表
     */
    private List<XEasyPdfTemplateTableCell> cells = new ArrayList<>(10);
}
