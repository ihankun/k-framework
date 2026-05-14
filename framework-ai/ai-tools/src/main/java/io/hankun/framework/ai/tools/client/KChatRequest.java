package io.hankun.framework.ai.tools.client;

import io.hankun.framework.ai.mcp.callback.KToolCallbackFetchFilter;
import io.hankun.framework.ai.mcp.callback.meta.ToolMetaUpdateInfo;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.tools.advisors.AdvisorConfig;
import io.hankun.framework.ai.tools.advisors.KAdvisorManager;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @className: KChatRequest
 * @createAt: 2025/7/2 11:13
 * @author: hankun
 */
public class KChatRequest {

    private final ChatClient chatClient;

    private final ChatModel chatModel;

    private final ChatRequestService chatRequestService;

    private final KAdvisorManager kAdvisorManager;


    public KChatRequest(ChatClientService chatClientService,
                        ChatRequestService chatRequestService,
                        KAdvisorManager kAdvisorManager,
                        ClientConfig config,
                        String model) {
        this.kAdvisorManager = kAdvisorManager;
        this.chatModel = chatClientService.buildChatModel(model);
        this.chatClient = chatClientService.buildChatClient(chatModel, config);
        this.chatRequestService = chatRequestService;
    }

    public Class<? extends ChatModel> getModelType() {
        return chatModel.getClass();
    }

    public ChatClient.ChatClientRequestSpec withAdvisors(ChatClient.ChatClientRequestSpec requestSpec, AdvisorConfig config) {
        return kAdvisorManager.withAdvisor(requestSpec, config);
    }

    public ChatClient.ChatClientRequestSpec call(ChatOptions options) {
        return chatRequestService.call(chatClient, new Prompt(List.of(), options));
    }

    public ChatClient.ChatClientRequestSpec call(Prompt prompt) {
        return chatRequestService.call(chatClient, prompt);
    }

    public ChatClient.ChatClientRequestSpec call(Prompt prompt, KToolContext context, Collection<ToolKey> tools) {
        return chatRequestService.callWithContext(chatClient, prompt, context, tools);
    }

    public ChatClient.ChatClientRequestSpec call(Prompt prompt, KToolContext context, Collection<ToolKey> tools,
                                                 List<ToolMetaUpdateInfo> updateInfos) {
        return chatRequestService.callWithContext(chatClient, prompt, context, tools, updateInfos);
    }

    public ChatClient.ChatClientRequestSpec call(KToolContext context, Collection<ToolKey> tools) {
        return chatRequestService.callWithContext(chatClient, null, context, tools);
    }

    public ChatClient.ChatClientRequestSpec call(Prompt prompt, KToolContext context, KToolCallbackFetchFilter filter) {
        return chatRequestService.callWithContext(chatClient, prompt, context, filter);
    }

    public ChatClient.ChatClientRequestSpec call(Prompt prompt, KToolContext context, KToolCallbackFetchFilter filter,
                                                 List<ToolMetaUpdateInfo> updateInfos) {
        return chatRequestService.callWithContext(chatClient, prompt, context, filter, updateInfos);
    }

    public ChatClient.ChatClientRequestSpec call(KToolContext context, KToolCallbackFetchFilter filter) {
        return chatRequestService.callWithContext(chatClient, null, context, filter);
    }
}
