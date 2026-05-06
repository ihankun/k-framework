package io.hankun.framework.ai.store.history.context;

import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.record.ChatRecord;
import io.hankun.framework.ai.common.session.SessionStatus;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.ContextData;
import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.entity.InputParams;
import io.hankun.framework.ai.context.register.ContextRegister;
import io.hankun.framework.ai.store.config.KAiStoreConfig;
import io.hankun.framework.ai.store.history.repository.TaskHistoryRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @className: ChatRecordContextRegister
 * @createAt: 2025/11/3 14:59
 * @author: hankun
 */
@Component
public class ChatRecordContextRegister implements ContextRegister<ChatRecordContext> {

    private final TaskHistoryRepository taskHistoryRepository;

    private final KAiStoreConfig kAiStoreConfig;

    public ChatRecordContextRegister(TaskHistoryRepository taskHistoryRepository,
                                     KAiStoreConfig kAiStoreConfig) {
        this.taskHistoryRepository = taskHistoryRepository;
        this.kAiStoreConfig = kAiStoreConfig;
    }

    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.MESSAGE;
    }

    @Override
    public String desc() {
        return "聊天记录";
    }

    @NotNull
    @Override
    public Class<ChatRecordContext> dataType() {
        return ChatRecordContext.class;
    }

    @Nullable
    @Override
    public ChatRecordContext build(@NotNull CurrentId currentId, @Nullable ChatRecordContext old, @NotNull ContextAccess contextAccess,
                                   @NotNull InputParams inputParams, @NotNull ContextData extendParams) {
        if (old != null) {
            return old;
        }
        List<ChatRecord> history = taskHistoryRepository.loadLocalHistory(currentId.sessionId(),
                kAiStoreConfig.getTaskHistoryCount());
        String input = inputParams.formatInput();
        String userKey = inputParams.getCallerInfo().uniqueId();
        ChatRecord chatRecord = ChatRecord.init(input, currentId, userKey);
        return new ChatRecordContext(history, chatRecord);
    }

    @NotNull
    @Override
    public String getKey() {
        return "chatRecord";
    }

    @Override
    public void onSessionUpdate(SessionStatus sessionStatus, CurrentId currentId, ChatRecordContext data, ContextAccess contextAccess) {
        if (sessionStatus.isFinal()) {
            taskHistoryRepository.save(data.currentRecord());
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
