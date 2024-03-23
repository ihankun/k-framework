package io.ihankun.framework.db.exceptions;


import io.ihankun.framework.common.v1.exception.BusinessException;

/**
 * @author hankun
 */
public class SqlFlowControlException extends BusinessException {

    public SqlFlowControlException(String message) {
        super(DbExceptionErrorCode.FLOW_CONTROL_ERROR, message);
    }
}
