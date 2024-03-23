package io.ihankun.framework.common.v1.tree.level.enums;

import lombok.Getter;

/**
 * @author hankun
 */

@Getter
public enum LevelEnum {

    /**
     * 一级
     */
    FIRST(1, "一级");

    /**
     * 级别
     */
    private Integer level;

    /**
     * 名称
     */
    private String value;

    LevelEnum(Integer level, String value) {
        this.level = level;
        this.value = value;
    }
}
