package io.hankun.framework.ai.tools.controller;

import io.hankun.framework.ai.store.memory.shortTerm.KRedisChatMemoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: MemoryController
 * @createAt: 2025/6/6 16:18
 * @author: hankun
 */
@Slf4j
@RestController
@RequestMapping("/memory")
public class MemoryController {


    private final KRedisChatMemoryRepository kRedisChatMemoryRepository;

    public MemoryController(KRedisChatMemoryRepository kRedisChatMemoryRepository) {
        this.kRedisChatMemoryRepository = kRedisChatMemoryRepository;
    }

    @GetMapping("/listMessage")
    public List<Message> listMessage(String conversationId) {
        return kRedisChatMemoryRepository.findByConversationId(conversationId);
    }

    @GetMapping("/listConversationId")
    public List<String> listConversationId() {
        return kRedisChatMemoryRepository.findConversationIds();
    }

    @GetMapping("/showExpire")
    public Map<String, Long> showExpire() {
        return kRedisChatMemoryRepository.showExpire();
    }

    @GetMapping("/listAllMessage")
    public Map<String, List<Message>> listAllMessage() {
        List<String> conversationIds = kRedisChatMemoryRepository.findConversationIds();
        Map<String, List<Message>> messages = new HashMap<>(conversationIds.size());
        for (String conversationId : conversationIds) {
            messages.put(conversationId, kRedisChatMemoryRepository.findByConversationId(conversationId));
        }
        return messages;
    }

    @PostMapping("/deleteConversationId")
    public void deleteConversationId(String conversationId) {
        kRedisChatMemoryRepository.deleteByConversationId(conversationId);
    }

    @PostMapping("/clear")
    public void clear() {
        kRedisChatMemoryRepository.clear();
    }
}
