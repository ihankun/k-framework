package io.ihankun.framework.db.exceptions;

import io.ihankun.framework.common.error.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum DbExceptionErrorCode implements IErrorCode {

    /**
     * 流控
     */
    FLOW_CONTROL_ERROR("90000","数据库繁忙,$1")

    ;

    private final String code;
    private final String msg;

    @Override
    public String prefix() {
        return "DB";
    }
}
