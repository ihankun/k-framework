package io.hankun.framework.ai.store.vector;

/**
 * @description:
 * @className: ContextStoreData
 * @createAt: 2025/12/4 14:35
 * @author: hankun
 */
public record ContextStoreData<T>(String id, String context, T meta) {
}
