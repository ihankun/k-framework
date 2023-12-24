package io.ihankun.framework.poi.view;

import io.ihankun.framework.poi.entity.vo.MapExcelGraphConstants;
import io.ihankun.framework.poi.excel.ExcelExportUtil;
import io.ihankun.framework.poi.excel.entity.ExportParams;
import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import io.ihankun.framework.poi.excel.graph.builder.ExcelChartBuildService;
import io.ihankun.framework.poi.excel.graph.entity.ExcelGraph;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Map 对象接口
 *
 * @author hankun
 */
@SuppressWarnings("unchecked")
@Controller(MapExcelGraphConstants.MAP_GRAPH_EXCEL_VIEW)
public class MapGraphExcelView extends MiniAbstractExcelView {

    public MapGraphExcelView() {
        super();
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        String codedFileName = "临时文件";
        ExportParams params=(ExportParams)model.get(MapExcelGraphConstants.PARAMS);
        List<ExcelExportEntity> entityList=(List<ExcelExportEntity>) model.get(MapExcelGraphConstants.ENTITY_LIST);
        List<Map<String, Object>> mapList=(List<Map<String, Object>>)model.get(MapExcelGraphConstants.MAP_LIST);
        List<ExcelGraph> graphDefinedList=(List<ExcelGraph>)model.get(MapExcelGraphConstants.GRAPH_DEFINED);
        //构建数据
        Workbook workbook = ExcelExportUtil.exportExcel(params,entityList,mapList);
        ExcelChartBuildService.createExcelChart(workbook,graphDefinedList, params.isDynamicData(), params.isAppendGraph());

        if (model.containsKey(MapExcelGraphConstants.FILE_NAME)) {
            codedFileName = (String) model.get(MapExcelGraphConstants.FILE_NAME);
        }
        out(workbook, codedFileName, request, response);
    }

}
