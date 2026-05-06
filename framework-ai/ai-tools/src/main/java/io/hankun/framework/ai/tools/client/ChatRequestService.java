package io.hankun.framework.ai.tools.client;

import io.hankun.framework.ai.mcp.callback.KToolCallbackFetchFilter;
import io.hankun.framework.ai.mcp.callback.KToolCallbackManager;
import io.hankun.framework.ai.mcp.callback.meta.ToolMetaUpdateInfo;
import io.hankun.framework.ai.mcp.callback.meta.ToolMetadataUpdateService;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.entity.ToolKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @className: ChatRequestService
 * @createAt: 2025/7/2 10:38
 * @author: hankun
 */
@Slf4j
@Component
public class ChatRequestService {

    private final KToolCallbackManager kToolCallbackManager;

    private final ToolMetadataUpdateService toolMetadataUpdateService;

    public ChatRequestService(KToolCallbackManager kToolCallbackManager,
                              ToolMetadataUpdateService toolMetadataUpdateService) {
        this.kToolCallbackManager = kToolCallbackManager;
        this.toolMetadataUpdateService = toolMetadataUpdateService;
    }

    public ChatClient.ChatClientRequestSpec call(ChatClient chatClient, Prompt prompt) {
        return callWithTools(chatClient, prompt, null, null, null);
    }

    public ChatClient.ChatClientRequestSpec callWithContext(ChatClient chatClient, Prompt prompt,
                                                            KToolContext context, KToolCallbackFetchFilter filter) {
        return callWithContext(chatClient, prompt, context, filter, null);
    }

    public ChatClient.ChatClientRequestSpec callWithContext(ChatClient chatClient, Prompt prompt,
                                                            KToolContext context, KToolCallbackFetchFilter filter,
                                                            List<ToolMetaUpdateInfo> updateInfos) {
        if (context == null) {
            throw new IllegalArgumentException("MsunToolContext cannot be null");
        }
        return callWithTools(chatClient, prompt, context, updateInfos, kToolCallbackManager.buildTools(filter));
    }

    public ChatClient.ChatClientRequestSpec callWithContext(ChatClient chatClient, Prompt prompt,
                                                            KToolContext context, Collection<ToolKey> tools) {
        return callWithContext(chatClient, prompt, context, tools, null);
    }

    public ChatClient.ChatClientRequestSpec callWithContext(ChatClient chatClient, Prompt prompt,
                                                            KToolContext context, Collection<ToolKey> tools,
                                                            List<ToolMetaUpdateInfo> updateInfos) {
        if (context == null) {
            throw new IllegalArgumentException("MsunToolContext cannot be null");
        }
        List<ToolCallback> toolCallbacks;
        if (CollectionUtils.isEmpty(tools)) {
            toolCallbacks = List.of();
        } else {
            toolCallbacks = kToolCallbackManager.buildTools(tools);
        }
        return callWithTools(chatClient, prompt, context, updateInfos, toolCallbacks);
    }


    public ChatClient.ChatClientRequestSpec callWithTools(ChatClient chatClient, Prompt prompt, KToolContext context,
                                                          List<ToolMetaUpdateInfo> updateInfos,
                                                          List<ToolCallback> tools) {
        tools = toolMetadataUpdateService.update(tools, updateInfos);
        ChatClient.ChatClientRequestSpec requestSpec;
        if (prompt != null) {
            requestSpec = chatClient.prompt(prompt);
        } else {
            requestSpec = chatClient.prompt();
        }
        if (context != null && !context.isEmpty()) {
            requestSpec.toolContext(context.toMap());
        }
        if (!CollectionUtils.isEmpty(tools)) {
            if (log.isDebugEnabled()) {
                log.debug("工具列表详细信息: {}", showTools(tools));
            }
            requestSpec.toolCallbacks(tools);
        }
        return requestSpec;
    }

    private static String showTools(List<ToolCallback> tools) {
        StringBuilder sb = new StringBuilder("\n");
        for (ToolCallback tool : tools) {
            sb.append("工具定义：").append(tool.getToolDefinition())
                    .append("；工具元数据").append(tool.getToolMetadata()).append("\n");
        }
        return sb.toString();
    }
}
