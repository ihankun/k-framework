package io.hankun.framework.ai.tools.observation;

import io.hankun.framework.ai.common.context.CurrentIdHolder;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.history.detail.ChatModelRecord;
import io.hankun.framework.ai.tools.contexts.ContextAccessHolder;
import io.hankun.framework.ai.tools.trace.TraceContext;
import io.hankun.framework.ai.tools.trace.TraceContextHolder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: ChatModelObservationHandler
 * @createAt: 2025/10/11 16:58
 * @author: hankun
 */
@Slf4j
@Component
public class ChatModelObservationHandler implements ObservationHandler<ChatModelObservationContext> {

    @Override
    public void onStart(@NotNull ChatModelObservationContext context) {
        //非流式 start->end->toolCall->start->end
        //流式 start->toolCall->start->end->end
        //流式下，toolCall返回后，缺失上下文
        //需要依赖@{TraceToolCallingInterceptor}解决问题
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        if (currentId == null) {
            return;
        }
        ContextAccess contextAccess = ContextAccessHolder.get();
        if (contextAccess == null) {
            return;
        }
        TraceContext before = TraceContextHolder.peek();
        //避免重复
        //当前由TraceToolCallingInterceptor完成创建，此处不可创建，避免非流式重复问题
        if (before != null && !before.isEnd()) {
            return;
        }
        ChatModelRecord record = ChatModelRecord.of(currentId);
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        TraceContext traceContext = TraceContextHolder.create(record, modelRecordContext.mapConfig());
        ChatOptions options = context.getRequest().getOptions();
        if (options != null) {
            traceContext.setModelName(options.getModel());
        }
        record.inputs().addAll(context.getRequest().getInstructions());
        modelRecordContext.add(record, traceContext.meta());
    }

    @Override
    public void onError(@NotNull ChatModelObservationContext context) {
        finish(context);
    }

    @Override
    public void onStop(@NotNull ChatModelObservationContext context) {
        finish(context);
    }

    @Override
    public boolean supportsContext(@NotNull Observation.Context context) {
        return context instanceof ChatModelObservationContext;
    }


    private void finish(ChatModelObservationContext context) {
        try {
            TraceContext traceContext = TraceContextHolder.pop();
            if (traceContext == null) {
                return;
            }
            //避免重复结束，流式下，受到栈式结构影响，存在连续触发finish的情况
            //start->toolCall->start->end->end
            if (traceContext.isEnd()) {
                return;
            }
            ChatResponse response = context.getResponse();
            if (response != null) {
                List<Generation> generations = response.getResults();
                List<Message> messages = new ArrayList<>(generations.size());
                for (Generation generation : generations) {
                    messages.add(generation.getOutput());
                }
                Usage usage = response.getMetadata().getUsage();
                traceContext.setInputTokens(usage.getPromptTokens());
                traceContext.setOutputTokens(usage.getCompletionTokens());
                traceContext.setOutputMessages(messages);
            }
            traceContext.end();
        } catch (Exception e) {
            log.error("ChatModelObservationHandler error", e);
        }
    }
}
