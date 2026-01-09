package io.hankun.framework.db.exceptions;


import io.hankun.framework.core.exception.BusinessException;

/**
 * @author hankun
 */
public class SqlFlowControlException extends BusinessException {

    public SqlFlowControlException(String message) {
        super(DbExceptionErrorCode.FLOW_CONTROL_ERROR, message);
    }
}
