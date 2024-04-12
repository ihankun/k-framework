package io.ihankun.framework.poi.excel.entity;

import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Data
public class ExportExcelItem {
	private List<ExcelExportEntity> entityList;
	private List<Map<String, Object>> resultList;
}
