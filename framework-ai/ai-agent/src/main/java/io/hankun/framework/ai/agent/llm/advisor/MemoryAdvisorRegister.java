package io.hankun.framework.ai.agent.llm.advisor;

import io.hankun.framework.ai.store.memory.shortTerm.KRedisChatMemoryRepository;
import io.hankun.framework.ai.tools.advisors.KAdvisorRegister;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @className: MemoryAdvisorRegister
 * @createAt: 2025/10/30 15:49
 * @author: hankun
 */
@Component
public class MemoryAdvisorRegister implements KAdvisorRegister {

    private final KRedisChatMemoryRepository kRedisChatMemoryRepository;

    public MemoryAdvisorRegister(KRedisChatMemoryRepository kRedisChatMemoryRepository) {
        this.kRedisChatMemoryRepository = kRedisChatMemoryRepository;
    }

    @Override
    public String name() {
        return "memory";
    }

    @Override
    public Advisor create(Map<String, Object> params) {
        ChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(kRedisChatMemoryRepository)
                .maxMessages(10)
                .build();
        return PromptChatMemoryAdvisor.builder(memory).order(-1).build();
    }
}
