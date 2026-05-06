package io.hankun.framework.ai.store.vector;

/**
 * @description:
 * @className: VectorSearchResult
 * @createAt: 2025/9/4 11:40
 * @author: hankun
 */
public record VectorSearchResult<T>(String key, T data, Float score) {
}
