package io.hankun.framework.ai.store.vector;

/**
 * @description:
 * @className: ContextSearchResult
 * @createAt: 2025/12/4 14:28
 * @author: hankun
 */
public record ContextSearchResult<T>(ContextStoreData<T> data, Float score) {
}
