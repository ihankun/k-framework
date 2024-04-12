package io.ihankun.framework.poi.excel.export.styler;

import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import io.ihankun.framework.poi.excel.entity.params.ExcelForEachParams;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 抽象接口提供两个公共方法
 *
 * @author hankun
 */
public abstract class AbstractExcelExportStyler implements IExcelExportStyler {
    //单行
    protected CellStyle stringNoneStyle;
    protected CellStyle stringNoneWrapStyle;
    //间隔行
    protected CellStyle stringSeptailStyle;
    protected CellStyle stringSeptailWrapStyle;

    protected Workbook workbook;

    protected static final short STRING_FORMAT = (short) BuiltinFormats.getBuiltinFormat("TEXT");

    protected void createStyles(Workbook workbook) {
        this.stringNoneStyle = stringNoneStyle(workbook, false);
        this.stringNoneWrapStyle = stringNoneStyle(workbook, true);
        this.stringSeptailStyle = stringSeptailStyle(workbook, false);
        this.stringSeptailWrapStyle = stringSeptailStyle(workbook, true);
        this.workbook = workbook;
    }

    @Override
    public CellStyle getStyles(boolean noneStyler, ExcelExportEntity entity) {
        if (noneStyler && (entity == null || entity.isWrap())) {
            return stringNoneWrapStyle;
        }
        if (noneStyler) {
            return stringNoneStyle;
        }
        if (noneStyler == false && (entity == null || entity.isWrap())) {
            return stringSeptailWrapStyle;
        }
        return stringSeptailStyle;
    }

    @Override
    public CellStyle getStyles(Cell cell, int dataRow, ExcelExportEntity entity, Object obj, Object data) {
        return getStyles(dataRow % 2 == 1, entity);
    }

    public CellStyle stringNoneStyle(Workbook workbook, boolean isWarp) {
        return null;
    }

    public CellStyle stringSeptailStyle(Workbook workbook, boolean isWarp) {
        return null;
    }


    @Override
    public CellStyle getTemplateStyles(boolean isSingle, ExcelForEachParams excelForEachParams) {
        return null;
    }

}
