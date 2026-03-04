package io.hankun.framework.poi.cache;

import io.hankun.framework.poi.excel.entity.ExcelToHtmlParams;
import io.hankun.framework.poi.excel.html.ExcelToHtmlService;

/**
 * Excel 转变成为Html 的缓存
 *
 * @author hankun
 */
public class HtmlCache {

    public static String getHtml(ExcelToHtmlParams params) {
        try {
            return new ExcelToHtmlService(params).printPage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
