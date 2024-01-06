package io.ihankun.framework.poi.excel.entity.result;

import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
 * 导入返回类
 *
 * @author hankun
 */
@Data
public class ExcelImportResult<T> {

    /**
     * 结果集
     */
    private List<T>  list;
    /**
     * 失败数据
     */
    private List<T>  failList;

    /**
     * 是否存在校验失败
     */
    private boolean  verifyFail;

    /**
     * 数据源
     */
    private Workbook workbook;
    /**
     * 失败的数据源
     */
    private Workbook failWorkbook;

    private Map<String,Object> map;

    public ExcelImportResult() {

    }

    public ExcelImportResult(List<T> list, boolean verifyFail, Workbook workbook) {
        this.list = list;
        this.verifyFail = verifyFail;
        this.workbook = workbook;
    }

}
