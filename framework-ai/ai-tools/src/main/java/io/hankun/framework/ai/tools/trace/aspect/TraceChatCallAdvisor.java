package io.hankun.framework.ai.tools.trace.aspect;

import io.hankun.framework.ai.tools.trace.TraceContext;
import io.hankun.framework.ai.tools.trace.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @description:
 * @className: TraceChatCallAdvisorRegister
 * @createAt: 2025/7/17 14:29
 * @author: hankun
 */
@Slf4j
@Component
public class TraceChatCallAdvisor implements BaseAdvisor {

    @NotNull
    @Override
    public ChatClientRequest before(@NotNull ChatClientRequest chatClientRequest, @NotNull AdvisorChain advisorChain) {
        return chatClientRequest;
    }

    @NotNull
    @Override
    public ChatClientResponse after(@NotNull ChatClientResponse chatClientResponse, @NotNull AdvisorChain advisorChain) {
        finish(chatClientResponse);
        return chatClientResponse;
    }

    @NotNull
    @Override
    public Flux<ChatClientResponse> adviseStream(@NotNull ChatClientRequest chatClientRequest,
                                                 @NotNull StreamAdvisorChain streamAdvisorChain) {
        Flux<ChatClientResponse> chatClientResponseFlux = Mono.just(chatClientRequest)
                .publishOn(getScheduler())
                .map(request -> this.before(request, streamAdvisorChain))
                .flatMapMany(streamAdvisorChain::nextStream);

        return chatClientResponseFlux.map(response -> {
            this.after(response, streamAdvisorChain);
            return response;
        }).onErrorResume(error -> Flux.error(new IllegalStateException("Stream processing failed", error)));
    }

    private static void finish(@NotNull ChatClientResponse chatClientResponse) {
        TraceContext traceContext = TraceContextHolder.peek();
        if (traceContext == null) {
            return;
        }
        traceContext.setFirstTokenTime(System.currentTimeMillis());
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
