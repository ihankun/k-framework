package io.hankun.framework.ai.tools.observation;

import io.hankun.framework.ai.core.context.CurrentIdHolder;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.record.TaskRecord;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.history.context.TaskRecordContext;
import io.hankun.framework.ai.store.history.detail.TaskToolCallRecord;
import io.hankun.framework.ai.tools.contexts.ContextAccessHolder;
import io.hankun.framework.ai.tools.trace.TraceContext;
import io.hankun.framework.ai.tools.trace.TraceContextHolder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.tool.observation.ToolCallingObservationContext;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: ToolCallObservationHandler
 * @createAt: 2025/10/11 16:45
 * @author: hankun
 */
@Slf4j
@Component
public class ToolCallObservationHandler implements ObservationHandler<ToolCallingObservationContext> {

    @Override
    public void onStart(@NotNull ToolCallingObservationContext context) {
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        if (currentId == null) {
            return;
        }
        ContextAccess contextAccess = ContextAccessHolder.get();
        if (contextAccess == null) {
            return;
        }
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        TraceContextHolder.create(TaskToolCallRecord.KEY, modelRecordContext.mapConfig());
    }

    @Override
    public void onError(@NotNull ToolCallingObservationContext context) {
        finish(context);
    }

    @Override
    public void onStop(@NotNull ToolCallingObservationContext context) {
        finish(context);
    }

    private void finish(ToolCallingObservationContext context) {
        try {
            TraceContext traceContext = TraceContextHolder.pop();
            if (traceContext == null) {
                return;
            }
            ContextAccess contextAccess = ContextAccessHolder.get();
            if (contextAccess == null) {
                return;
            }
            traceContext.end();
            CurrentId currentId = CurrentIdHolder.getCurrentId();
            TaskToolCallRecord toolCallRecord = TaskToolCallRecord.of(currentId, context.getToolDefinition().name(),
                    context.getToolCallArguments(), context.getToolCallResult());
            ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
            modelRecordContext.add(toolCallRecord, traceContext.meta());
            TaskRecordContext taskRecordContext = contextAccess.getData(TaskRecordContext.class);
            TaskRecord taskRecord = taskRecordContext.load(currentId.agentId());
            if (taskRecord != null) {
                taskRecord.addToolCall(currentId, toolCallRecord.toolName(),
                        toolCallRecord.params(), toolCallRecord.result());
            }
        } catch (Exception e) {
            log.error("ToolCallObservationHandler error", e);
        }
    }

    @Override
    public boolean supportsContext(@NotNull Observation.Context context) {
        return context instanceof ToolCallingObservationContext;
    }
}
