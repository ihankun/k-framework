package io.ihankun.framework.db.exceptions;


import io.ihankun.framework.core.error.IErrorCode;
import io.ihankun.framework.core.exception.BusinessException;

/**
 * @author hankun
 */
public class TableAuthException extends BusinessException {

    private final String errorMessage;

    public TableAuthException(String message) {
        super(new IErrorCode() {
            @Override
            public String prefix() {
                return "kun-db-table";
            }

            @Override
            public String getCode() {
                return "9998";
            }

            @Override
            public String getMsg() {
                return message;
            }
        });
        this.errorMessage = message;
    }

    @Override
    public String toString() {
        return "TableAuthException(权限错误，" + errorMessage + ")";
    }
}
