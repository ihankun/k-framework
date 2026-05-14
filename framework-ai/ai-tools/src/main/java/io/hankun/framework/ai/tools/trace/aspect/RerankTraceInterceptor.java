package io.hankun.framework.ai.tools.trace.aspect;

import io.hankun.framework.ai.common.context.CurrentIdHolder;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.model.interceptors.RerankInterceptor;
import io.hankun.framework.ai.model.rerank.KDocumentWithScore;
import io.hankun.framework.ai.model.rerank.KRerankModel;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.history.detail.RerankRecord;
import io.hankun.framework.ai.tools.contexts.ContextAccessHolder;
import io.hankun.framework.ai.tools.trace.TraceContext;
import io.hankun.framework.ai.tools.trace.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: RerankTraceInterceptor
 * @createAt: 2025/8/29 13:38
 * @author: hankun
 */
@Slf4j
@Component
public class RerankTraceInterceptor implements RerankInterceptor {

    @Override
    public List<KDocumentWithScore> rerank(KRerankModel rerankModel, String query, List<Document> documents, int topN) {
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        if (currentId == null) {
            return rerankModel.rerank(query, documents, topN);
        }
        ContextAccess contextAccess = ContextAccessHolder.get();
        if (contextAccess == null) {
            return rerankModel.rerank(query, documents, topN);
        }
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        RerankRecord rerankRecord = RerankRecord.of(currentId, query, topN);
        TraceContext traceContext = TraceContextHolder.create(rerankRecord, modelRecordContext.mapConfig());
        modelRecordContext.add(rerankRecord, traceContext.meta());
        List<KDocumentWithScore> result = rerankModel.rerank(query, documents, topN);
        for (KDocumentWithScore documentWithScore : result) {
            rerankRecord.addResult(documentWithScore.document().getText(), documentWithScore.score());
        }
        try {
            traceContext.end();
            TraceContextHolder.pop();
        } catch (Exception e) {
            log.error("RerankTraceInterceptor error", e);
        }
        return result;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
