package io.ihankun.framework.common.exception.impl;

import io.ihankun.framework.common.exception.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@AllArgsConstructor
@Getter
public enum CommonErrorCode implements IErrorCode {

    /**
     * 上级不存在
     */
    UP_LEVEL_NOT_FOUND("0001", "上级不存在"),
    /**
     * 返回数据为空
     */
    RESULT_NULL("0002", "返回数据为空"),
    /**
     * 日期格式错误
     */
    DATE_FORMAT_ERROR("0003", "日期格式错误"),
    /**
     * 当前用户未登录
     */
    USER_NOT_LOGIN("0004", "当前用户未登录"),
    /**
     * 请求限流
     */
    TOMCAT_RATE_LIMITED("0007", "接口请求频率过高，触发限流策略，释放时间:$1"),


    ;
    private String code;
    private String msg;

    @Override
    public String prefix() {
        return "common";
    }
}
