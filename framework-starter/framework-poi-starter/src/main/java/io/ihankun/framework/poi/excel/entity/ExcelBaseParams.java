package io.ihankun.framework.poi.excel.entity;

import io.ihankun.framework.poi.handler.inter.ICommentHandler;
import io.ihankun.framework.poi.handler.inter.IExcelDataHandler;
import io.ihankun.framework.poi.handler.inter.IExcelDictHandler;
import io.ihankun.framework.poi.handler.inter.II18nHandler;
import lombok.Data;

/**
 * 基础参数
 *
 * @author hankun
 */
@Data
public class ExcelBaseParams {

    /**
     * 数据处理接口,以此为主,replace,format都在这后面
     */
    private IExcelDataHandler dataHandler;

    /**
     * 字段处理类
     */
    private IExcelDictHandler dictHandler;
    /**
     * 国际化处理类
     */
    private II18nHandler i18nHandler;
    /**
     * 批注处理类
     */
    private ICommentHandler commentHandler;

}
