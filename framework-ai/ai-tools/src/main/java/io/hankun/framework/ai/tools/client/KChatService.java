package io.hankun.framework.ai.tools.client;

import io.hankun.framework.ai.tools.advisors.KAdvisorManager;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: MsunChatService
 * @createAt: 2025/7/2 11:08
 * @author: hankun
 */
@Component
public class KChatService {

    private final ChatClientService chatClientService;

    private final ChatRequestService chatRequestService;

    private final KAdvisorManager kAdvisorManager;

    public KChatService(ChatClientService chatClientService,
                        ChatRequestService chatRequestService,
                        KAdvisorManager kAdvisorManager) {
        this.chatClientService = chatClientService;
        this.chatRequestService = chatRequestService;
        this.kAdvisorManager = kAdvisorManager;
    }

    public KChatRequest buildRequest(ClientConfig config, String model) {
        return new KChatRequest(chatClientService, chatRequestService, kAdvisorManager, config, model);
    }

    public KChatRequest buildRequest(ClientConfig config) {
        return buildRequest(config, null);
    }

}
