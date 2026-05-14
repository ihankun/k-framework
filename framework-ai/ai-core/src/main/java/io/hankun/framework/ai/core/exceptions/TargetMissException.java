package io.hankun.framework.ai.core.exceptions;

import lombok.Getter;

/**
 * @description:
 * @className: TargetMissException
 * @createAt: 2025/10/16 14:24
 * @author: hankun
 */
@Getter
public class TargetMissException extends RuntimeException {
    private final String type;

    private final Object argument;

    public TargetMissException(String type, Object argument) {
        this.type = type;
        this.argument = argument;
    }

    @Override
    public String getMessage() {
        return "目标缺失，" + type + "【" + argument + "】不存在";
    }
}
