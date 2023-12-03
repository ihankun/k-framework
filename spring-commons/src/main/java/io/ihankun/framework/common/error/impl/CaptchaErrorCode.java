package io.ihankun.framework.common.error.impl;

import io.ihankun.framework.common.error.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CaptchaErrorCode implements IErrorCode {

    SUCCESS("200", "OK"),
    NOT_VALID_PARAM("403", "无效参数"),
    INTERNAL_SERVER_ERROR("500", "未知的内部错误"),
    EXPIRED("4000", "已失效"),
    BASIC_CHECK_FAIL("4001", "基础校验失败"),
    DEFINITION("50001", "basic check fail"),
    ;

    private String code;
    private String msg;

    @Override
    public String prefix() {
        return "captcha";
    }
}
