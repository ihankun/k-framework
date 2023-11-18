package io.ihankun.framework.spring.server.error;

import io.ihankun.framework.common.exception.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum AdviceErrorCode implements IErrorCode {

    /**
     * 数据绑定异常
     */
    BIND_EX("0001", "数据绑定异常,$1"),
    /**
     * 参数校验失败错误
     */
    PARAM_NOT_VALID_EX("0002", "参数校验失败错误,$1"),
    /**
     * 数据验证异常
     */
    CONSTRAINT_EX("0003", "数据验证异常,$1"),
    /**
     * 系统异常
     */
    OTHER_EX("0004", "系统异常"),

    /**
     * 客户端错误
     */
    CLIENT_ERROR("0005", "客户端错误");

    private String code;
    private String msg;

    @Override
    public String prefix() {
        return "advice";
    }
}
