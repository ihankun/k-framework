package io.hankun.framework.poi.view;

import io.hankun.framework.poi.entity.vo.MapExcelConstants;
import io.hankun.framework.poi.excel.ExcelExportUtil;
import io.hankun.framework.poi.excel.entity.ExportParams;
import io.hankun.framework.poi.excel.entity.params.ExcelExportEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Map 对象接口
 *
 * @author hankun
 */
@SuppressWarnings("unchecked")
@Controller(MapExcelConstants.EASYPOI_MAP_EXCEL_VIEW)
public class EasypoiMapExcelView extends MiniAbstractExcelView {

    public EasypoiMapExcelView() {
        super();
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        String codedFileName = "临时文件";
        Workbook workbook = ExcelExportUtil.exportExcel(
            (ExportParams) model.get(MapExcelConstants.PARAMS),
            (List<ExcelExportEntity>) model.get(MapExcelConstants.ENTITY_LIST),
            (Collection<? extends Map<?, ?>>) model.get(MapExcelConstants.MAP_LIST));
        if (model.containsKey(MapExcelConstants.FILE_NAME)) {
            codedFileName = (String) model.get(MapExcelConstants.FILE_NAME);
        }
        out(workbook, codedFileName, request, response);
    }

}
