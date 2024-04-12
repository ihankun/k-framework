package io.ihankun.framework.poi.handler.inter;

import io.ihankun.framework.poi.excel.entity.result.ExcelVerifyHandlerResult;

/**
 * 导入校验接口
 *
 * @author hankun
 */
public interface IExcelVerifyHandler<T> {

    /**
     * 导入校验方法
     *
     * @param obj
     *            当前对象
     * @return
     */
    public ExcelVerifyHandlerResult verifyHandler(T obj);

}
