package io.hankun.framework.ai.core.entity;

/**
 * @description:
 * @className: ExecResult
 * @createAt: 2025/6/5 20:31
 * @author: hankun
 */
public record ExecResult<T>(boolean success, T result) {

    public static <T> ExecResult<T> success(T result) {
        return new ExecResult<>(true, result);
    }

    public static <T> ExecResult<T> fail() {
        return new ExecResult<>(false, null);
    }
}
