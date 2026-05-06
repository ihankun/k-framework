package io.hankun.framework.ai.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * @description:
 * @className: ArgumentErrorType
 * @createAt: 2025/10/16 15:07
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum ArgumentErrorType {
    MISS("miss", "不可为空"),

    INCOMPLETE("incomplete", "不完整"),

    STRUCT_ERROR("struct-error", "结构错误"),

    TYPE_ERROR("type-error", "类型错误"),

    SHOULD_EMPTY("should-empty", "应当为空"),

    ;

    private final String code;
    private final String desc;

    public String buildArgumentDesc(String argument) {
        String part = "";
        if (StringUtils.hasText(argument)) {
            part = "【" + argument + "】";
        }
        return "参数" + part + desc;
    }
}
