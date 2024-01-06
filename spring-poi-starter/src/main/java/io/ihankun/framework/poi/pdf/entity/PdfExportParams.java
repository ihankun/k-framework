package io.ihankun.framework.poi.pdf.entity;

import io.ihankun.framework.poi.excel.entity.ExcelBaseParams;
import io.ihankun.framework.poi.pdf.styler.IPdfExportStyler;
import lombok.Data;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * PDF 导出参数设置
 *
 * @author hankun
 */
@Data
public class PdfExportParams extends ExcelBaseParams {

    /**
     * 表格名称
     */
    private String title;

    /**
     * 表格名称
     */
    private short titleHeight = 30;

    /**
     * 第二行名称
     */
    private String secondTitle;

    /**
     * 表格名称
     */
    private short secondTitleHeight = 25;
    /**
     * 过滤的属性
     */
    private String[] exclusions;
    /**
     * 是否添加需要需要
     */
    private boolean addIndex;
    /**
     * 是否添加需要需要
     */
    private String indexName = "序号";

    private PDRectangle pageSize = PDRectangle.A4;

    private IPdfExportStyler styler;

    public PdfExportParams() {

    }

    public PdfExportParams(String title) {
        this.title = title;
    }

    public PdfExportParams(String title, String secondTitle) {
        this.title = title;
        this.secondTitle = secondTitle;
    }

}
