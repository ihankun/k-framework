package io.hankun.framework.ai.agent.llm;

import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.agent.node.result.NodeResultReader;
import io.hankun.framework.ai.core.context.CurrentIdHolder;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.mcp.callback.meta.ToolMetaUpdateInfo;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import io.hankun.framework.ai.tools.client.KChatRequest;
import io.hankun.framework.commons.context.KContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * @description:
 * @className: LlmCall
 * @createAt: 2025/10/21 10:46
 * @author: hankun
 */
@Slf4j
public record LlmCall(CurrentId currentId, ActionConfig config, ChatOptions chatOptions,
                      List<String> tools, List<Message> messages, KToolContext toolContext,
                      KChatRequest kChatRequest, List<ToolMetaUpdateInfo> updateInfos) {


    public static String getContentFromChatResponse(@Nullable ChatResponse chatResponse) {
        return Optional.ofNullable(chatResponse)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AbstractMessage::getText)
                .orElse("");
    }

    public Flux<DataWithMeta> callWithNodeConvert(NodeResultReader resultReader) {
        Flux<ChatResponse> chatResponseFlux = call();
        return resultReader.convert(chatResponseFlux.map(LlmCall::getContentFromChatResponse));
    }

    public Flux<String> callWithText() {
        Flux<ChatResponse> chatResponseFlux = call();
        return chatResponseFlux.map(LlmCall::getContentFromChatResponse);
    }

    public Flux<ChatResponse> call() {
        if (config.getStream()) {
            return steamCall();
        } else {
            return noStreamCall();
        }
    }

    private ChatClient.ChatClientRequestSpec buildRequestSpec() {
        log.info("工具列表：{}", tools);
        return kChatRequest.call(new Prompt(messages, chatOptions),
                        toolContext, ToolKey.of(tools), updateInfos)
                .advisors(a -> {
                    a.param(ChatMemory.CONVERSATION_ID, currentId.sessionId());
                });
    }

    private Flux<ChatResponse> noStreamCall() {
        ChatResponse chatResponse = buildRequestSpec().call().chatResponse();
        if (chatResponse == null) {
            return Flux.empty();
        }
        return Flux.just(chatResponse);
    }

    private Flux<ChatResponse> steamCall() {
        Flux<ChatResponse> flux = buildRequestSpec().stream().chatResponse();
        flux = flux.contextWrite(KContextHolder.captureReactorContext());
        return flux;
    }

    public static class Builder {

        private final CurrentId currentId;

        private final ActionConfig config;

        private final ChatOptions chatOptions;

        private final PromptInfo basePrompt;

        private String expandPrompt;

        private final KToolContext toolContext;

        private final KChatRequest kChatRequest;

        @Getter
        private String user;

        private final List<String> tools = new ArrayList<>();

        private final List<Message> messages = new ArrayList<>();


        private final List<ToolMetaUpdateInfo> updateInfos = new ArrayList<>();


        private final Map<String, Object> params = new HashMap<>();

        private final Map<String, Object> userParams = new HashMap<>();

        private final Map<String, Object> userContexts = new HashMap<>();

        private final List<Message> continueMessages = new ArrayList<>();

        private boolean replaceContext;

        private Builder(CurrentId currentId,
                        ActionConfig config,
                        ChatOptions chatOptions,
                        PromptInfo basePrompt,
                        KToolContext toolContext,
                        KChatRequest kChatRequest,
                        String user) {
            this.currentId = currentId;
            this.config = config;
            this.chatOptions = chatOptions;
            this.basePrompt = basePrompt;
            this.toolContext = toolContext;
            this.kChatRequest = kChatRequest;
            this.user = user;
        }

        public Builder setExpandPrompt(String expandPrompt) {
            this.expandPrompt = expandPrompt;
            return this;
        }

        public Builder addTool(String tool) {
            if (tools.contains(tool)) {
                return this;
            }
            tools.add(tool);
            return this;
        }

        public Builder setTools(List<String> tools) {
            this.tools.clear();
            this.tools.addAll(tools);
            return this;
        }

        public Builder addMessage(Message message) {
            messages.add(message);
            return this;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public Builder setMessages(List<Message> messages) {
            this.messages.clear();
            this.messages.addAll(messages);
            return this;
        }

        public Builder addUpdateInfo(ToolMetaUpdateInfo updateInfo) {
            updateInfos.add(updateInfo);
            return this;
        }

        public Builder setUpdateInfos(List<ToolMetaUpdateInfo> updateInfos) {
            this.updateInfos.clear();
            this.updateInfos.addAll(updateInfos);
            return this;
        }

        public Builder addParam(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public Builder setParams(Map<String, Object> params) {
            this.params.clear();
            this.params.putAll(params);
            return this;
        }

        public Builder addUserParam(String key, Object value) {
            userParams.put(key, value);
            return this;
        }

        public Builder setUserParams(Map<String, Object> userParams) {
            this.userParams.clear();
            this.userParams.putAll(userParams);
            return this;
        }

        public Builder addUserContext(String key, Object value) {
            userContexts.put(key, value);
            return this;
        }

        public Builder setUserContexts(Map<String, Object> userContexts) {
            this.userContexts.clear();
            this.userContexts.putAll(userContexts);
            return this;
        }

        /**
         * 继续对话
         *
         * @param messages       历史发生的消息
         * @param replaceContext 是否对用户上下文进行刷新
         * @return this
         */
        public Builder continueWithMessages(List<Message> messages, boolean replaceContext) {
            if (CollectionUtils.isEmpty(messages)) {
                return this;
            }
            this.continueMessages.clear();
            this.continueMessages.addAll(messages);
            this.replaceContext = replaceContext;
            return this;
        }

        public LlmCall build() {
            List<Message> processMessages = new ArrayList<>();
            if (CollectionUtils.isEmpty(continueMessages)) {
                Message systemMessage = LlmMessageBuilder.buildPrompt(basePrompt, params, expandPrompt);
                if (systemMessage != null) {
                    processMessages.add(systemMessage);
                    if (!CollectionUtils.isEmpty(userContexts)) {
                        processMessages.add(LlmMessageBuilder.buildUserContext(userContexts));
                    }
                } else {
                    log.error("[LlmCall] 系统提示词丢失，currentId:{},code={}", CurrentIdHolder.getCurrentId(), config.getPromptCode());
                }
                if (!CollectionUtils.isEmpty(messages)) {
                    processMessages.addAll(messages);
                }
            } else {
                processMessages.addAll(continueMessages);
                //替换上下文信息
                if (replaceContext) {
                    //消息数量大于等于2
                    if (continueMessages.size() >= 2) {
                        //第一个消息是系统消息
                        if (continueMessages.getFirst().getMessageType().equals(MessageType.SYSTEM)) {
                            //第二个消息是用户消息
                            if (continueMessages.get(1).getMessageType().equals(MessageType.USER)) {
                                //判断第二个消息是否是上下文消息
                                if (continueMessages.get(1).getText().startsWith(LlmMessageBuilder.USER_CONTEXT_PREFIX)) {
                                    //替换上下文信息
                                    processMessages.set(1, LlmMessageBuilder.buildUserContext(userContexts));
                                    log.info("[LlmCall] 替换用户上下文: {}", userContexts);
                                }
                            }
                        }
                    }
                }
            }

            Message userMessage = LlmMessageBuilder.buildUser(user, userParams);
            if (userMessage != null) {
                processMessages.add(userMessage);
            }
            return new LlmCall(currentId, config, chatOptions, tools, processMessages,
                    toolContext, kChatRequest, updateInfos);
        }
    }

    public static Builder newBuilder(CurrentId currentId,
                                     ActionConfig config,
                                     ChatOptions chatOptions,
                                     PromptInfo basePrompt,
                                     KToolContext toolContext,
                                     KChatRequest kChatRequest,
                                     String user) {
        return new Builder(currentId, config, chatOptions, basePrompt, toolContext, kChatRequest, user);
    }
}
