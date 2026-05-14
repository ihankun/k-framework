package io.hankun.framework.ai.tools.client;

import io.hankun.framework.ai.model.KModelManager;
import io.hankun.framework.ai.tools.advisors.KAdvisorManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: ChatClientService
 * @createAt: 2025/5/29 09:25
 * @author: hankun
 */
@Slf4j
@Component
public class ChatClientService {

    private final KModelManager kModelManager;

    private final KAdvisorManager kAdvisorManager;

    public ChatClientService(KModelManager kModelManager,
                             KAdvisorManager kAdvisorManager) {
        this.kModelManager = kModelManager;
        this.kAdvisorManager = kAdvisorManager;
    }

    public ChatModel buildChatModel(String group) {
        return kModelManager.getChatModel(group);
    }

    public ChatClient buildChatClient(String group, ClientConfig config) {
        return buildChatClient(kModelManager.getChatModel(group), config);
    }


    public ChatClient buildChatClient(ChatModel chatModel,
                                      ClientConfig config) {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel);
        kAdvisorManager.withDefAdvisor(chatClientBuilder, config.advisorConfig());
        return chatClientBuilder.build();
    }
}
