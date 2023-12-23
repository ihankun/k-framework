package io.ihankun.framework.poi.csv.entity;

import io.ihankun.framework.poi.excel.entity.ExcelBaseParams;
import lombok.Data;

/**
 * CSV 导入参数
 *
 * @author by jueyue on 18-10-3.
 */
@Data
public class CsvExportParams extends ExcelBaseParams {

    public static final String UTF8 = "utf-8";
    public static final String GBK = "gbk";
    public static final String GB2312 = "gb2312";

    private String encoding = UTF8;

    /**
     * 分隔符
     */
    private String spiltMark = ",";

    /**
     * 字符串标识符
     */
    private String textMark = "\"";

    /**
     * 表格标题行数,默认0
     */
    private int titleRows = 0;

    /**
     * 表头行数,默认1
     */
    private int headRows = 1;
    /**
     * 过滤的属性
     */
    private String[] exclusions;

    /**
     * 是否创建表头
     */
    private boolean isCreateHeadRows = true;

    public CsvExportParams() {

    }

    public CsvExportParams(String encoding) {
        this.encoding = encoding;
    }

}
