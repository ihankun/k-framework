package io.hankun.framework.ai.store.history.po;

import io.hankun.framework.ai.core.record.ChatRecord;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @className: ChatHistory
 * @createAt: 2025/11/3 14:26
 * @author: hankun
 */
@Data
@Document(collection = "chatHistory")
@CompoundIndex(name = "sessionId_id_index", def = "{'sessionId':1, 'id':1}", unique = true)
@CompoundIndex(name = "sessionId_taskId_index", def = "{'sessionId':1, 'taskId':1}")
public class ChatHistory {
    @Id
    private ObjectId id;
    @Indexed
    private String userKey;
    @Indexed
    private String sessionId;
    @Indexed
    private String taskId;
    @Indexed
    private String messageId;

    private String userMessage;

    private String assistantMessage;

    private Map<String, Object> meta;

    private Date startTime;

    private Long timeCost = -1L;

    public static ChatHistory of(ChatRecord chatRecord) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(new ObjectId());
        chatHistory.setUserKey(chatRecord.getUserKey());
        chatHistory.setSessionId(chatRecord.getSessionId());
        chatHistory.setTaskId(chatRecord.getTaskId());
        chatHistory.setMessageId(chatRecord.getMessageId());
        chatHistory.setUserMessage(chatRecord.getUserMessage());
        chatHistory.setAssistantMessage(chatRecord.getAssistantMessage());
        chatHistory.setMeta(chatRecord.getMeta());
        chatHistory.setStartTime(chatRecord.getCreateTime());
        chatHistory.setTimeCost(chatRecord.getTimeCost());
        return chatHistory;
    }

    public static ChatRecord toChatRecord(ChatHistory chatHistory) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setSessionId(chatHistory.getSessionId());
        chatRecord.setUserKey(chatHistory.getUserKey());
        chatRecord.setTaskId(chatHistory.getTaskId());
        chatRecord.setMessageId(chatHistory.getMessageId());
        chatRecord.setUserMessage(chatHistory.getUserMessage());
        chatRecord.setAssistantMessage(chatHistory.getAssistantMessage());
        chatRecord.setMeta(chatHistory.getMeta());
        chatRecord.setCreateTime(chatHistory.getStartTime());
        chatRecord.setTimeCost(chatHistory.getTimeCost());
        return chatRecord;
    }
}
