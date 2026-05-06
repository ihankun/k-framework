package io.hankun.framework.ai.tools.controller;

import io.hankun.framework.ai.context.entity.CallerInfo;
import io.hankun.framework.ai.store.history.TaskHistoryService;
import io.hankun.framework.ai.store.history.vo.SessionInfoVo;
import io.hankun.framework.ai.store.history.vo.UserChatVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @className: UserChatController
 * @createAt: 2025/10/20 11:39
 * @author: hankun
 */
@Slf4j
@RestController
@RequestMapping("/userChat")
public class UserChatController {

    private final TaskHistoryService taskHistoryService;

    public UserChatController(TaskHistoryService taskHistoryService) {
        this.taskHistoryService = taskHistoryService;
    }

    @GetMapping("/listChatMemory")
    public List<UserChatVo> listChatMemory(@RequestParam(required = true) String sessionId,
                                           @RequestParam(required = false) String cursor,
                                           @RequestParam(required = false, defaultValue = "30") Integer size) {
        log.info("listChatMemory: {}, {}, {}", sessionId, cursor, size);
        return taskHistoryService.fetchUserChatVoBySessionId(sessionId, cursor, size);
    }


    @GetMapping("/listSession")
    public List<SessionInfoVo> listSession() {
        return taskHistoryService.listSessionInfoVoByUserKey(CallerInfo.buildUserKey());
    }
}
