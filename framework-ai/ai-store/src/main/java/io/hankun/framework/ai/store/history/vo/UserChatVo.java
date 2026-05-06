package io.hankun.framework.ai.store.history.vo;

import io.hankun.framework.ai.common.record.ChatRecord;
import io.hankun.framework.ai.store.history.po.ChatHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: UserChatVo
 * @createAt: 2025/10/20 11:24
 * @author: hankun
 */
public record UserChatVo(String userMessage, String assistantMessage, Map<String, Object> meta, Date date) {


    public static List<UserChatVo> of(List<ChatHistory> records) {
        if (records == null) {
            return null;
        }
        List<UserChatVo> userChatVos = new ArrayList<>(records.size());
        for (ChatHistory record : records) {
            UserChatVo userChatVo = of(record);
            userChatVos.add(userChatVo);
        }
        return userChatVos;
    }

    public static UserChatVo of(ChatHistory record) {
        ChatRecord chatRecord = ChatHistory.toChatRecord(record);
        return new UserChatVo(chatRecord.getUserMessage(), chatRecord.getAssistantMessage(),
                chatRecord.getMeta(), chatRecord.getCreateTime());
    }
}

