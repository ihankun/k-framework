package io.ihankun.framework.poi.view;

import io.ihankun.framework.poi.entity.vo.BigExcelConstants;
import io.ihankun.framework.poi.excel.ExcelExportUtil;
import io.ihankun.framework.poi.excel.entity.ExportParams;
import io.ihankun.framework.poi.handler.inter.IExcelExportServer;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author hankun
 *
 * Excel 生成解析器,减少用户操作
 */
@Controller(BigExcelConstants.EASYPOI_BIG_EXCEL_VIEW)
public class EasypoiBigExcelExportView extends MiniAbstractExcelView {

    public EasypoiBigExcelExportView() {
        super();
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        String codedFileName = "临时文件";
        Workbook workbook = ExcelExportUtil.exportBigExcel(
                (ExportParams) model.get(BigExcelConstants.PARAMS),
                (Class<?>) model.get(BigExcelConstants.CLASS),
                (IExcelExportServer) model.get(BigExcelConstants.DATA_INTER),
                model.get(BigExcelConstants.DATA_PARAMS));
        if (model.containsKey(BigExcelConstants.FILE_NAME)) {
            codedFileName = (String) model.get(BigExcelConstants.FILE_NAME);
        }
        out(workbook, codedFileName, request, response);
    }
}
