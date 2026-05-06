package io.hankun.framework.ai.mem.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: MemType
 * @createAt: 2025/12/4 13:58
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum MemType {

    FACT("fact", "事实"),

    SUMMARY("summary", "总结"),

    NEWEST("newest", "最新"),

    USER("user", "用户"),

    ;
    private final String code;
    private final String desc;
}
