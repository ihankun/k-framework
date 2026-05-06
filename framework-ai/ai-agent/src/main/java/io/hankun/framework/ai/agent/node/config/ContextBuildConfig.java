package io.hankun.framework.ai.agent.node.config;

import io.hankun.framework.ai.model.prompt.PromptInfo;
import io.hankun.framework.ai.tools.client.KChatRequest;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.Map;

/**
 * @description:
 * @className: ContextBuildConfig
 * @createAt: 2025/10/24 14:17
 * @author: hankun
 */
public record ContextBuildConfig(ChatOptions chooseOptions, Map<String, PromptInfo> choosePrompts,
                                 KChatRequest kChatRequest, boolean stream) {

}
