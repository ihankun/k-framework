package io.ihankun.framework.poi.word.entity.params;

import io.ihankun.framework.poi.excel.entity.ExcelBaseParams;
import io.ihankun.framework.poi.handler.inter.IExcelDataHandler;

import java.util.List;

/**
 * Excel 导出对象
 *
 * @author hankun
 */
public class ExcelListEntity extends ExcelBaseParams {

    /**
     * 数据源
     */
    private List<?>  list;

    /**
     * 实体类对象
     */
    private Class<?> clazz;

    /**
     * 表头行数
     */
    private int      headRows = 1;

    public ExcelListEntity() {

    }

    public ExcelListEntity(List<?> list, Class<?> clazz) {
        this.list = list;
        this.clazz = clazz;
    }

    public ExcelListEntity(List<?> list, Class<?> clazz, IExcelDataHandler dataHandler) {
        this.list = list;
        this.clazz = clazz;
        setDataHandler(dataHandler);
    }

    public ExcelListEntity(List<?> list, Class<?> clazz, IExcelDataHandler dataHandler,
                           int headRows) {
        this.list = list;
        this.clazz = clazz;
        this.headRows = headRows;
        setDataHandler(dataHandler);
    }

    public ExcelListEntity(List<?> list, Class<?> clazz, int headRows) {
        this.list = list;
        this.clazz = clazz;
        this.headRows = headRows;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public int getHeadRows() {
        return headRows;
    }

    public List<?> getList() {
        return list;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void setHeadRows(int headRows) {
        this.headRows = headRows;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

}
