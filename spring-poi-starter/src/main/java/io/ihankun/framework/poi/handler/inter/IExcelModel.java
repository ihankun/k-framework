package io.ihankun.framework.poi.handler.inter;

/**
 * Excel标记类
 *
 * @author hankun
 */
public interface IExcelModel {

    /**
     * 获取错误数据
     * @return
     */
    public String getErrorMsg();

    /**
     *  设置错误信息
     * @param errorMsg
     */
    public void setErrorMsg(String errorMsg);

}
