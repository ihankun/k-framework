package io.ihankun.framework.poi.excel;

import io.ihankun.framework.poi.cache.HtmlCache;
import io.ihankun.framework.poi.excel.entity.ExcelToHtmlParams;
import io.ihankun.framework.poi.excel.entity.enmus.ExcelType;
import io.ihankun.framework.poi.excel.html.HtmlToExcelService;
import io.ihankun.framework.poi.exception.excel.ExcelExportException;
import io.ihankun.framework.poi.exception.excel.enums.ExcelExportEnum;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Excel 变成界面
 *
 * @author hankun
 */
public class ExcelXorHtmlUtil {

    private ExcelXorHtmlUtil() {
    }

    /**
     * 转换成为Table
     * @param wb Excel
     * @return
     */
    public static String toTableHtml(Workbook wb) {
        return HtmlCache.getHtml(new ExcelToHtmlParams(wb, false, 0, null));
    }

    /**
     * 转换成为完整界面,显示图片
     * @param wb Excel
     * @return
     */
    public static String toAllHtml(Workbook wb) {
        return HtmlCache.getHtml(new ExcelToHtmlParams(wb, true, 0, null));
    }

    /**
     * 转换成为Table
     * @param params
     * @return
     */
    public static String excelToHtml(ExcelToHtmlParams params) {
        return HtmlCache.getHtml(params);
    }

    /**
     * Html 读取Excel
     * @param html
     * @param type
     * @return
     */
    public static Workbook htmlToExcel(String html, ExcelType type) {
        Workbook workbook = null;
        if (ExcelType.HSSF.equals(type)) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        new HtmlToExcelService().createSheet(html, workbook);
        return workbook;
    }

    /**
     * Html 读取Excel
     * @param is
     * @param type
     * @return
     */
    public static Workbook htmlToExcel(InputStream is, ExcelType type) {
        try {
            return htmlToExcel(new String(IOUtils.toByteArray(is)), type);
        } catch (IOException e) {
            throw new ExcelExportException(ExcelExportEnum.HTML_ERROR, e);
        }
    }

}
