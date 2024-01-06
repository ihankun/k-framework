package io.ihankun.framework.poi.pdf.styler;

import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.structure.cell.TextCell;

/**
 * PDF导出样式设置
 *
 * @author hankun
 */
public interface IPdfExportStyler {

    /**
     * 设置Cell的样式
     *
     * @param entity
     * @param text
     */
    public void setCellStyler(TextCell.TextCellBuilder iCell, ExcelExportEntity entity, String text);

    /**
     * 获取字体
     *
     * @param entity
     * @param text
     */
    public PDFont getFont(ExcelExportEntity entity, String text);

    /**
     * 获取字体
     */
    public PDFont getFont();
}
