package io.hankun.framework.ai.tools.model;

import io.hankun.framework.ai.core.util.FluxUtil;
import io.hankun.framework.ai.tools.advisors.AdvisorConfig;
import io.hankun.framework.ai.tools.client.KChatRequest;
import io.hankun.framework.commons.context.KContextHolder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class AiFunction {

    private final KChatRequest chatRequest;

    private final PromptTemplate promptTemplate;

    private final Function<ChatClient.ChatClientRequestSpec, ChatClient.ChatClientRequestSpec> requestCustomizer;


    public AiFunction(KChatRequest chatRequest, String promptText) {
        this(chatRequest, promptText, null);
    }

    public AiFunction(KChatRequest chatRequest, String promptText, Function<ChatClient.ChatClientRequestSpec, ChatClient.ChatClientRequestSpec> requestCustomizer) {
        this(chatRequest, PromptTemplate.builder()
                .template(promptText)
                .build(), requestCustomizer);
    }

    public AiFunction(KChatRequest chatRequest, PromptTemplate promptTemplate) {
        this(chatRequest, promptTemplate, null);
    }

    public AiFunction(KChatRequest chatRequest, PromptTemplate promptTemplate,
                      Function<ChatClient.ChatClientRequestSpec, ChatClient.ChatClientRequestSpec> requestCustomizer) {
        this.chatRequest = chatRequest;
        this.promptTemplate = promptTemplate;
        this.requestCustomizer = requestCustomizer;
    }

    public ChatClient.ChatClientRequestSpec buildRequest(ChatOptions options, Map<String, Object> params,
                                                         AdvisorConfig config) {
        ChatClient.ChatClientRequestSpec requestSpec = chatRequest.call(options);
        requestSpec = requestSpec
                .user(prompt -> prompt.text(promptTemplate.getTemplate()).params(params));
        requestSpec = chatRequest.withAdvisors(requestSpec, config);
        if (requestCustomizer != null) {
            requestSpec = requestCustomizer.apply(requestSpec);
        }
        return requestSpec;
    }

    public String call(ChatOptions options, Map<String, Object> params) {
        return call(options, params, null);
    }


    public String call(ChatOptions options, Map<String, Object> params, AdvisorConfig config) {
        ChatClient.ChatClientRequestSpec requestSpec = buildRequest(options, params, config);
        return requestSpec.call().content();
    }

    public String streamWait(ChatOptions options, Map<String, Object> params) {
        return streamWait(options, params, null, null);
    }

    public String streamWait(ChatOptions options, Map<String, Object> params, Consumer<String> listener, AdvisorConfig config) {
        Flux<String> stringFlux = stream(options, params, config);
        stringFlux = stringFlux.contextWrite(KContextHolder.captureReactorContext());
        if (listener != null) {
            stringFlux.doOnNext(listener);
        }
        return FluxUtil.collectToString(stringFlux);
    }

    public Flux<String> stream(ChatOptions options, Map<String, Object> params) {
        return stream(options, params, null);
    }

    public Flux<String> stream(ChatOptions options, Map<String, Object> params, AdvisorConfig config) {
        ChatClient.ChatClientRequestSpec requestSpec = buildRequest(options, params, config);
        return requestSpec.stream().content();
    }
}
