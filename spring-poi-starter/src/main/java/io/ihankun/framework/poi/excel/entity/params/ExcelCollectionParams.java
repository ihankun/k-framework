package io.ihankun.framework.poi.excel.entity.params;

import lombok.Data;

import java.util.Map;

/**
 * Excel 对于的 Collection
 *
 * @author hankun
 */
@Data
public class ExcelCollectionParams {

    /**
     * 集合对应的名称
     */
    private String                         name;
    /**
     * Excel 列名称
     */
    private String                         excelName;
    /**
     * 实体对象
     */
    private Class<?>                       type;
    /**
     * 这个list下面的参数集合实体对象
     */
    private Map<String, ExcelImportEntity> excelParams;

}
