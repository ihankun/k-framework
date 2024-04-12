package io.ihankun.framework.mq.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    /**
     * 普通消息
     */
    NORMAL("normal", "普通消息"),
    /**
     * 广播消息
     */
    BOARD_CAST("boardCast", "广播消息"),
    /**
     * 顺序消息
     */
    ORDER("order", "顺序消息"),
    /**
     * 事务消息
     */
    TRANSLATION("translation", "事务消息");

    private String type;

    private String desc;

    public static MessageTypeEnum convert(String type) {
        switch (type) {
            case "normal":
                return NORMAL;
            case "order":
                return ORDER;
            case "translation":
                return TRANSLATION;
            case "boardCast":
                return BOARD_CAST;
            default:
                return NORMAL;
        }
    }
}
