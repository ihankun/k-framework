package io.hankun.framework.ai.tools.observation;

import io.hankun.framework.ai.common.context.CurrentIdHolder;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.history.detail.EmbeddingRecord;
import io.hankun.framework.ai.tools.contexts.ContextAccessHolder;
import io.hankun.framework.ai.tools.trace.TraceContext;
import io.hankun.framework.ai.tools.trace.TraceContextHolder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationContext;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: EmbeddingObservationHandler
 * @createAt: 2025/10/11 16:59
 * @author: hankun
 */
@Slf4j
@Component
public class EmbeddingObservationHandler implements ObservationHandler<EmbeddingModelObservationContext> {

    @Override
    public void onStart(@NotNull EmbeddingModelObservationContext context) {
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        if (currentId == null) {
            return;
        }
        ContextAccess contextAccess = ContextAccessHolder.get();
        if (contextAccess == null) {
            return;
        }
        EmbeddingRecord embeddingRecord = EmbeddingRecord.of(currentId, context.getRequest().getInstructions());
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        TraceContext traceContext = TraceContextHolder.create(embeddingRecord,modelRecordContext.mapConfig());
        modelRecordContext.add(embeddingRecord, traceContext.meta());
    }

    @Override
    public void onError(@NotNull EmbeddingModelObservationContext context) {
        finish(context);
    }

    @Override
    public void onStop(@NotNull EmbeddingModelObservationContext context) {
        finish(context);
    }

    private static void finish(EmbeddingModelObservationContext context) {
        try {
            TraceContext traceContext = TraceContextHolder.pop();
            if (traceContext == null) {
                return;
            }
            traceContext.end();
            // 获取方法参数
            if (context.getResponse() != null) {
                Usage usage = context.getResponse().getMetadata().getUsage();
                traceContext.setModelName(context.getResponse().getMetadata().getModel());
                traceContext.setInputTokens(usage.getPromptTokens());
                traceContext.setOutputTokens(usage.getCompletionTokens());
            }
        } catch (Exception e) {
            log.error("EmbeddingObservationHandler error", e);
        }
    }

    @Override
    public boolean supportsContext(@NotNull Observation.Context context) {
        return context instanceof EmbeddingModelObservationContext;
    }
}
