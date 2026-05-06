package io.hankun.framework.ai.tools.trace.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: DetailLevel
 * @createAt: 2025/8/29 11:53
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum DetailLevel {
    NONE("none", "无", 0),
    BASE("base", "基础", 1),
    DETAIL("detail", "详细", 2),
    ALL("all", "全部", 3),
    ;

    private final String level;
    private final String desc;
    private final int code;

    public boolean detailThan(DetailLevel detailLevel) {
        return this.code > detailLevel.code;
    }

    public static DetailLevel getByCode(String code) {
        for (DetailLevel value : values()) {
            if (value.level.equals(code)) {
                return value;
            }
        }
        return DetailLevel.NONE;
    }
}
