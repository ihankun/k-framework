package io.hankun.framework.ai.core.record;

import io.hankun.framework.ai.core.entity.CurrentId;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @className: ChatRecord
 * @createAt: 2025/10/16 10:30
 * @author: hankun
 */
@Data
public class ChatRecord {
    private String sessionId;
    private String userKey;
    private String taskId;
    private String messageId;
    private String userMessage;
    private String assistantMessage;
    private Map<String, Object> meta;
    private Date createTime;
    private Long timeCost = -1L;

    public static ChatRecord init(String userMessage, CurrentId currentId, String userKey) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setSessionId(currentId.sessionId());
        chatRecord.setUserKey(userKey);
        chatRecord.setTaskId(currentId.taskId());
        chatRecord.setMessageId(currentId.messageId());
        chatRecord.setUserMessage(userMessage);
        chatRecord.setCreateTime(new Date());
        chatRecord.setTimeCost(-1L);
        return chatRecord;
    }

    public void finish(String assistantMessage, Map<String, Object> meta) {
        this.assistantMessage = assistantMessage;
        this.meta = meta;
        this.timeCost = System.currentTimeMillis() - createTime.getTime();
    }
}
