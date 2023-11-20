package io.ihankun.framework.common.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;

/**
 * @author hankun
 */
@Slf4j
@Data
public class BusinessException extends RuntimeException{

    /**
     * 业务code
     */
    private String code;

    /**
     * 错误前缀
     */
    private String prefix;

    private static final String REPLACE_STR = "$";

    /**
     * 根据错误code、错误信息构造业务异常
     */
    public static BusinessException build(String message, String... params) {
        return new BusinessException("common", "common", executeMsg(message, params), params);
    }

    /**
     * 根据错误code、错误信息构造业务异常
     */
    public static BusinessException build(IErrorCode errorCode, String... params) {
        return new BusinessException(errorCode.prefix(), errorCode.getCode(), executeMsg(errorCode.getMsg(), params), params);
    }

    /**
     * 构造业务异常
     *
     * @param prefix  异常前缀
     * @param code    异常编码
     * @param message 异常信息
     * @param params  异常参数
     */
    public static BusinessException build(String prefix, String code, String message, String... params) {
        return new BusinessException(prefix, code, message, params);
    }

    /**
     * 构造业务异常
     *
     * @param prefix  异常前缀
     * @param code    异常编码
     * @param message 异常信息
     */
    public static BusinessException build(String prefix, String code, String message) {
        return new BusinessException(prefix, code, message);
    }

    /**
     * 处理消息
     */
    private static String executeMsg(String msg, String... params) {
        try {
            if (msg.contains(REPLACE_STR) && params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    String param = params[i];
                    if (param != null) {
                        msg = msg.replaceAll("\\$" + (i + 1), Matcher.quoteReplacement(param));
                    }
                }
            }
            return msg;
        } catch (Exception e) {
            log.error("invoke executeMsg error", e);
            return msg;
        }
    }

    /**
     * 根据错误code、错误信息构造业务异常
     *
     * @param code    业务code
     * @param message 错误信息
     */
    protected BusinessException(String prefix, String code, String message) {
        super(executeMsg(message, null));
        this.prefix = prefix;
        this.code = code;
    }

    /**
     * 根据错误code、错误信息构造业务异常
     *
     * @param code    业务code
     * @param message 错误信息
     */
    protected BusinessException(String prefix, String code, String message, String[] params) {
        super(executeMsg(message, null));
        this.prefix = prefix;
        this.code = code;
    }


    /**
     * 构造业务异常
     */
    protected BusinessException(IErrorCode errorCode) {
        super(executeMsg(errorCode.getMsg(), null));
        this.prefix = errorCode.prefix();
        this.code = errorCode.getCode();
    }

    /**
     * 构造业务异常
     */
    protected BusinessException(IErrorCode errorCode, String... params) {
        super(executeMsg(errorCode.getMsg(), params));
        this.prefix = errorCode.prefix();
        this.code = errorCode.getCode();
    }
}
