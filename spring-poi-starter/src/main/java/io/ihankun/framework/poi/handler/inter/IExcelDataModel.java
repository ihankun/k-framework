package io.ihankun.framework.poi.handler.inter;

/**
 * Excel 本身数据文件
 *
 * @author hankun
 */
public interface IExcelDataModel {

    /**
     * 获取行号
     * @return
     */
    public Integer getRowNum();

    /**
     *  设置行号
     * @param rowNum
     */
    public void setRowNum(Integer rowNum);

}
