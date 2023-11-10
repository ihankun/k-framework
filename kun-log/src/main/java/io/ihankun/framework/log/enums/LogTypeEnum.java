package io.ihankun.framework.log.enums;

import lombok.Getter;

/**
 * @author hankun
 */
@Getter
public enum LogTypeEnum {
    /**
     * http请求
     */
    HTTP("http"),

    /**
     * 数据库请求
     */
    DB("db"),

    /**
     * 远程调用
     */
    API("api"),

    /**
     * mq发送请求
     */
    MQ("mq"),

    /**
     * 手动日志
     */
    INNER("inner");

    /**
     * 日志类型的值
     */
    private final String value;

    LogTypeEnum(String value) {
        this.value = value;
    }
}
