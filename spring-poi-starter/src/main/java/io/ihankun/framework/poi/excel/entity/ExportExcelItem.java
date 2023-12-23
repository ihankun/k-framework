/**
 *
 */
package io.ihankun.framework.poi.excel.entity;

import io.ihankun.framework.poi.excel.entity.params.ExcelExportEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author xfworld
 * @since 2016-5-26
 * @version 1.0
 */
@Data
public class ExportExcelItem {
	private List<ExcelExportEntity> entityList;
	private List<Map<String, Object>> resultList;
}
