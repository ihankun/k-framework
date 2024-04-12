package io.ihankun.framework.poi.excel.html.css.impl;

import io.ihankun.framework.poi.excel.html.css.ICssConvertToExcel;
import io.ihankun.framework.poi.excel.html.css.ICssConvertToHtml;
import io.ihankun.framework.poi.excel.html.entity.style.CellStyleEntity;
import io.ihankun.framework.poi.util.PoiCssUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;


/**
 * 行高转换实现类
 *
 * @author hankun
 */
public class HeightCssConverImpl implements ICssConvertToExcel, ICssConvertToHtml {

    @Override
    public String convertToHtml(Cell cell, CellStyle cellStyle, CellStyleEntity style) {

        return null;
    }

    @Override
    public void convertToExcel(Cell cell, CellStyle cellStyle, CellStyleEntity style) {
        if (StringUtils.isNoneBlank(style.getHeight())) {
            int height = Math.round(PoiCssUtils.getInt(style.getHeight()) * 255 / 12.75F);
            Row row = cell.getRow();
            if (height > row.getHeight()) {
                row.setHeight((short) height);
            }
        }
    }

}
