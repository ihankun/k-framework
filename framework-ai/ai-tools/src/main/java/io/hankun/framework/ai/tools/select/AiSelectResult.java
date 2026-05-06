package io.hankun.framework.ai.tools.select;

import io.hankun.framework.ai.tools.select.entity.SelectBy;

import java.util.List;

/**
 * @description:
 * @className: AiSelectResult
 * @createAt: 2025/9/2 17:50
 * @author: hankun
 */
public record AiSelectResult<T>(SelectBy selectBy, List<T> option) {

    public static <T> AiSelectResult<T> directMatch(T option) {
        return new AiSelectResult<>(SelectBy.DIRECT_MATCH, List.of(option));
    }

    public static <T> AiSelectResult<T> rerankMatch(T option) {
        return new AiSelectResult<>(SelectBy.RERANK_MATCH, List.of(option));
    }

    public static <T> AiSelectResult<T> aiChoose(T option) {
        return new AiSelectResult<>(SelectBy.AI_CHOOSE, List.of(option));
    }

    public static <T> AiSelectResult<T> aiChoose(List<T> options) {
        return new AiSelectResult<>(SelectBy.AI_CHOOSE, options);
    }

    public static <T> AiSelectResult<T> notFind(T option) {
        return new AiSelectResult<>(SelectBy.NOT_FIND, option == null ? null : List.of(option));
    }

    public static <T> AiSelectResult<T> onlyOne(T option) {
        return new AiSelectResult<>(SelectBy.ONLY_ONE, List.of(option));
    }

    public static <T> AiSelectResult<T> aiFailed() {
        return new AiSelectResult<>(SelectBy.AI_FAILED, null);
    }
}
