package io.hankun.framework.ai.tools.trace.aspect;

import io.hankun.framework.ai.common.context.CurrentIdHolder;
import io.hankun.framework.ai.common.context.KContextManager;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.store.history.context.ModelRecordContext;
import io.hankun.framework.ai.store.history.detail.ChatModelRecord;
import io.hankun.framework.ai.tools.contexts.ContextAccessHolder;
import io.hankun.framework.ai.tools.toolcall.intercept.ToolCallingInterceptChain;
import io.hankun.framework.ai.tools.toolcall.intercept.ToolCallingInterceptor;
import io.hankun.framework.ai.tools.trace.TraceContext;
import io.hankun.framework.ai.tools.trace.TraceContextHolder;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: TraceToolCallingInterceptor
 * @createAt: 2025/11/6 10:27
 * @author: hankun
 */
@Component
public class TraceToolCallingInterceptor implements ToolCallingInterceptor {

    private final KContextManager kContextManager;

    public TraceToolCallingInterceptor(KContextManager kContextManager) {
        this.kContextManager = kContextManager;
    }

    @Override
    public ToolExecutionResult intercept(@NotNull Prompt prompt, @NotNull ChatResponse chatResponse,
                                         ToolCallingInterceptChain chain) {
        KContext old = KContextHolder.get();
        try {
            if (old == null) {
                KToolContext kToolContext = buildToolContext(prompt);
                KContext kContext = kContextManager.get(kToolContext.getContextKey());
                KContextHolder.set(kContext);
            }
            TraceContext traceContext = TraceContextHolder.peek();
            if (traceContext != null) {
                if (ChatModelRecord.KEY.equals(traceContext.type())) {
                    List<Generation> generations = chatResponse.getResults();
                    List<Message> messages = new ArrayList<>(generations.size());
                    for (Generation generation : generations) {
                        messages.add(generation.getOutput());
                    }
                    Usage usage = chatResponse.getMetadata().getUsage();
                    traceContext.setInputTokens(usage.getPromptTokens());
                    traceContext.setOutputTokens(usage.getCompletionTokens());
                    traceContext.setOutputMessages(messages);
                    traceContext.end();
                }
            }
            ToolExecutionResult result = chain.call(prompt, chatResponse);
            if (!result.returnDirect()) {
                createNext(chatResponse, result);
            }
            return result;
        } finally {
            KContextHolder.set(old);
        }
    }

    private void createNext(ChatResponse chatResponse, ToolExecutionResult executionResult) {
        CurrentId currentId = CurrentIdHolder.getCurrentId();
        if (currentId == null) {
            return;
        }
        ContextAccess contextAccess = ContextAccessHolder.get();
        if (contextAccess == null) {
            return;
        }
        ChatModelRecord record = ChatModelRecord.of(currentId);
        record.inputs().addAll(executionResult.conversationHistory());
        ModelRecordContext modelRecordContext = contextAccess.getData(ModelRecordContext.class);
        TraceContext traceContext = TraceContextHolder.create(record, modelRecordContext.mapConfig());
        modelRecordContext.add(record, traceContext.meta());
        Usage usage = chatResponse.getMetadata().getUsage();
        traceContext.setInputOffset(usage.getPromptTokens());
        traceContext.setOutputOffset(usage.getCompletionTokens());
    }

    private static KToolContext buildToolContext(Prompt prompt) {
        Map<String, Object> toolContextMap = Map.of();

        if (prompt.getOptions() instanceof ToolCallingChatOptions toolCallingChatOptions
                && !CollectionUtils.isEmpty(toolCallingChatOptions.getToolContext())) {
            toolContextMap = new HashMap<>(toolCallingChatOptions.getToolContext());
        }
        return new KToolContext(toolContextMap);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
