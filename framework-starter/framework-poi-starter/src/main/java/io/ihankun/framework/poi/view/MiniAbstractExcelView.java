package io.ihankun.framework.poi.view;

import io.ihankun.framework.poi.util.WebFilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基础抽象Excel View
 *
 * @author hankun
 */
public abstract class MiniAbstractExcelView extends PoiBaseView {

    private static final String CONTENT_TYPE = "text/html;application/vnd.ms-excel";

    protected static final String HSSF = ".xls";
    protected static final String XSSF = ".xlsx";

    public MiniAbstractExcelView() {
        setContentType(CONTENT_TYPE);
    }


    public void out(Workbook workbook, String codedFileName, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        if (workbook instanceof HSSFWorkbook) {
            codedFileName += HSSF;
        } else {
            codedFileName += XSSF;
        }
        // 用工具类生成符合RFC 5987标准的文件名header, 去掉UA判断
        response.setHeader("content-disposition", WebFilenameUtils.disposition(codedFileName));
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

}
