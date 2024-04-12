package io.ihankun.framework.poi.excel.entity.result;

import lombok.Data;

/**
 * Excel导入处理返回结果
 *
 * @author hankun
 */
@Data
public class ExcelVerifyHandlerResult {
    /**
     * 是否正确
     */
    private boolean success;
    /**
     * 错误信息
     */
    private String  msg;

    public ExcelVerifyHandlerResult() {

    }

    public ExcelVerifyHandlerResult(boolean success) {
        this.success = success;
    }

    public ExcelVerifyHandlerResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

}
