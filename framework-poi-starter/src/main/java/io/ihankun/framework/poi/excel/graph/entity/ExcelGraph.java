package io.ihankun.framework.poi.excel.graph.entity;

import java.util.List;

/**
 * @author hankun
 */
public interface ExcelGraph
{
	public ExcelGraphElement getCategory();
	public List<ExcelGraphElement> getValueList();
	public Integer getGraphType();
	public List<ExcelTitleCell> getTitleCell();
	public List<String> getTitle();
}
