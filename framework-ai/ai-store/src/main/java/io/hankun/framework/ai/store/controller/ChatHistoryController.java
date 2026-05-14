package io.hankun.framework.ai.store.controller;

import io.hankun.framework.ai.store.history.po.ChatHistory;
import io.hankun.framework.ai.store.history.po.SessionInfo;
import io.hankun.framework.ai.store.history.repository.ChatHistoryRepository;
import io.hankun.framework.ai.store.history.repository.SessionInfoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hankun
 * @date 2025/11/1711:19
 */
@RestController
@RequestMapping("/history")
public class ChatHistoryController {
    private final ChatHistoryRepository chatHistoryRepository;
    private final SessionInfoRepository sessionInfoRepository;

    public ChatHistoryController(ChatHistoryRepository chatHistoryRepository,
                                 SessionInfoRepository sessionInfoRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.sessionInfoRepository = sessionInfoRepository;
    }

    /**
     * 加载会话数据
     * @param sessionId 会话ID
     * @return 会话数据
     */
    @GetMapping("/chatHistory/loadChatData")
    public List<ChatHistory> loadChatData(@RequestParam(required = false) String sessionId) {

        return chatHistoryRepository.loadChatData(sessionId);

    }

    /**
     * 查询一段时间内的会话数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 会话数据
     */
    @GetMapping("/sessionInfo/loadSessionData")
    public List<SessionInfo> loadSessionData(@RequestParam(required = true) String startTime,
                                             @RequestParam(required = true) String endTime) {
        return sessionInfoRepository.loadSessionInfoData(startTime, endTime);
    }
}
