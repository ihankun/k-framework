package io.hankun.framework.ai.store.history.detail;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.record.IDetailRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: RerankRecord
 * @createAt: 2025/10/17 17:43
 * @author: hankun
 */
public record RerankRecord(String type, CurrentId currentId, String query, int topN,
                           List<RerankResult> rerankResult) implements IDetailRecord {

    public static final String KEY = "rerank";

    public void addResult(String text, double score) {
        rerankResult.add(new RerankResult(text, score));
    }


    public record RerankResult(String text, double score) {

    }

    public static RerankRecord of(CurrentId currentId, String query, int topN) {
        return new RerankRecord(KEY, currentId, query, topN, new ArrayList<>());
    }
}
