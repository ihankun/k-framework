package io.ihankun.framework.poi.excel.html.css;

import io.ihankun.framework.poi.excel.html.entity.style.CellStyleEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * CSS Cell Style 转换类
 *
 * @author hankun
 */
public interface ICssConvertToHtml {
	/**
	 * 把Excel单元格样式转换成HTML样式
	 * @param cell
	 *
	 */
    public String convertToHtml(Cell cell, CellStyle cellStyle, CellStyleEntity style);

}
