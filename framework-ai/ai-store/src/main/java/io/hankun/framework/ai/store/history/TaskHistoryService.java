package io.hankun.framework.ai.store.history;

import io.hankun.framework.ai.store.history.po.ChatHistory;
import io.hankun.framework.ai.store.history.po.SessionInfo;
import io.hankun.framework.ai.store.history.repository.TaskHistoryRepository;
import io.hankun.framework.ai.store.history.vo.SessionInfoVo;
import io.hankun.framework.ai.store.history.vo.UserChatVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: TaskHistoryService
 * @createAt: 2025/10/20 11:23
 * @author: hankun
 */
@Component
public class TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;

    public TaskHistoryService(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    public List<SessionInfoVo> listSessionInfoVoByUserKey(String userKey) {
        List<SessionInfo> sessionInfos = taskHistoryRepository.getSessionByUserKey(userKey);
        return SessionInfoVo.of(sessionInfos);
    }

    public List<UserChatVo> fetchUserChatVoBySessionId(String sessionId, String cursor, int count) {
        List<ChatHistory> taskHistories = taskHistoryRepository.fetchBefore(sessionId, cursor, false, count
                , "chatRecord");
        return UserChatVo.of(taskHistories);
    }


    public List<ChatHistory> fetchTaskHistory(String sessionId,String taskId){
        return taskHistoryRepository.fetchTaskHistory(sessionId,taskId);
    }
}
