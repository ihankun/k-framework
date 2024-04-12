package io.ihankun.framework.poi.excel.html.css.impl;

import io.ihankun.framework.poi.excel.html.css.ICssConvertToExcel;
import io.ihankun.framework.poi.excel.html.css.ICssConvertToHtml;
import io.ihankun.framework.poi.excel.html.entity.style.CellStyleEntity;
import io.ihankun.framework.poi.util.PoiCssUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 列宽转换实现类
 *
 * @author hankun
 */
public class WidthCssConverImpl implements ICssConvertToExcel, ICssConvertToHtml {

    @Override
    public String convertToHtml(Cell cell, CellStyle cellStyle, CellStyleEntity style) {

        return null;
    }

    @Override
    public void convertToExcel(Cell cell, CellStyle cellStyle, CellStyleEntity style) {
        if (StringUtils.isNoneBlank(style.getWidth())) {
            int width = (int) Math.round(PoiCssUtils.getInt(style.getWidth()) * 2048 / 8.43F);
            Sheet sheet = cell.getSheet();
            int colIndex = cell.getColumnIndex();
            if (width > sheet.getColumnWidth(colIndex)) {
                if (width > 255 * 256) {
                    width = 255 * 256;
                }
                sheet.setColumnWidth(colIndex, width);
            }
        }
    }

}
