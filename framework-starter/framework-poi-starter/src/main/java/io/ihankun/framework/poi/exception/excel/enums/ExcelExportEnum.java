package io.ihankun.framework.poi.exception.excel.enums;

/**
 * 导出异常类型枚举
 *
 * @author hankun
 */
public enum ExcelExportEnum {

        PARAMETER_ERROR ("Excel 导出   参数错误") ,
        EXPORT_ERROR ("Excel导出错误") ,
        HTML_ERROR ("Excel导出Html流错误") ,
        TEMPLATE_ERROR ("Excel 模板错误");

    private String msg;

    ExcelExportEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
