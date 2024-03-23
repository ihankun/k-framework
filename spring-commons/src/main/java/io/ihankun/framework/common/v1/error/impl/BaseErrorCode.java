package io.ihankun.framework.common.v1.error.impl;

import io.ihankun.framework.common.v1.error.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@AllArgsConstructor
@Getter
public enum BaseErrorCode implements IErrorCode {

    /**
     * 成功
     */
    SUCCESS("0000", "成功"),
    /**
     * 系统自动熔断
     */
    FALLBACK("system@9998", "系统错误,$1"),
    /**
     * 系统错误
     */
    SYSTEM_ERROR("system@9999", "系统繁忙");


    private String code;
    private String msg;

    @Override
    public String prefix() {
        return "";
    }
}
