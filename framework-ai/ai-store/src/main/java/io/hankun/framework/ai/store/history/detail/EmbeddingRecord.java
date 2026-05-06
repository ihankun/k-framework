package io.hankun.framework.ai.store.history.detail;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.record.IDetailRecord;

import java.util.List;

/**
 * @description:
 * @className: EmbeddingRecord
 * @createAt: 2025/10/17 17:42
 * @author: hankun
 */
public record EmbeddingRecord(String type, CurrentId currentId, List<String> input) implements IDetailRecord {

    public static final String KEY = "embedding";

    public static EmbeddingRecord of(CurrentId currentId, List<String> input) {
        return new EmbeddingRecord(KEY, currentId, input);
    }
}
