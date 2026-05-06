package io.hankun.framework.ai.store.history.context;

import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.common.entity.CurrentId;
import io.hankun.framework.ai.common.record.IDetailRecord;
import io.hankun.framework.ai.common.record.RecordHolder;
import io.hankun.framework.ai.common.session.task.MapConfig;
import io.hankun.framework.ai.store.history.detail.ChatModelRecord;
import io.hankun.framework.ai.store.history.detail.EmbeddingRecord;
import io.hankun.framework.ai.store.history.detail.RerankRecord;
import io.hankun.framework.ai.store.history.detail.TaskToolCallRecord;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description:
 * @className: ModelRecordContext
 * @createAt: 2025/10/16 19:02
 * @author: hankun
 */
public record ModelRecordContext(
        List<RecordHolder<ChatModelRecord>> chatModelRecords,
        List<RecordHolder<EmbeddingRecord>> embeddingRecords,
        List<RecordHolder<RerankRecord>> rerankRecords,
        List<RecordHolder<TaskToolCallRecord>> taskToolCallRecords,
        MapConfig mapConfig) implements IContext {


    public void add(ChatModelRecord record, Map<String, Object> meta) {
        chatModelRecords.add(new RecordHolder<>(record, meta));
    }

    public void add(EmbeddingRecord record, Map<String, Object> meta) {
        embeddingRecords.add(new RecordHolder<>(record, meta));
    }

    public void add(RerankRecord record, Map<String, Object> meta) {
        rerankRecords.add(new RecordHolder<>(record, meta));
    }

    public void add(TaskToolCallRecord record, Map<String, Object> meta) {
        taskToolCallRecords.add(new RecordHolder<>(record, meta));
    }

    public List<Message> fetchCurrentMessage(CurrentId currentId) {
        if (chatModelRecords.isEmpty()) {
            return List.of();
        }
        RecordHolder<ChatModelRecord> beforeNode = fetchCurrent(currentId, chatModelRecords);
        if (beforeNode == null) {
            return List.of();
        }
        List<Message> outputs = beforeNode.record().loadOutputs(beforeNode.meta());
        List<Message> allMessages = new ArrayList<>(beforeNode.record().inputs().size() +
                outputs.size());
        allMessages.addAll(beforeNode.record().inputs());
        allMessages.addAll(outputs);
        return allMessages;
    }

    public static <T extends IDetailRecord> RecordHolder<T> fetchCurrent(CurrentId currentId, List<RecordHolder<T>> records) {
        for (int i = records.size() - 1; i >= 0; i--) {
            RecordHolder<T> recordHolder = records.get(i);
            CurrentId recordId = recordHolder.record().currentId();
            if (recordId.equals(currentId)) {
                return recordHolder;
            }
        }
        return null;
    }

    public static <T extends IDetailRecord> T fetchBefore(CurrentId currentId, List<RecordHolder<T>> records) {
        for (int i = records.size() - 1; i >= 0; i--) {
            RecordHolder<T> recordHolder = records.get(i);
            CurrentId recordId = recordHolder.record().currentId();
            if (currentId.sessionId().equals(recordId.sessionId()) &&
                    currentId.taskId().equals(recordId.taskId()) &&
                    currentId.agentId().equals(recordId.agentId()) &&
                    currentId.nodeId().equals(recordId.beforeNodeId())) {
                return recordHolder.record();
            }
        }
        return null;
    }

    public static ModelRecordContext of(MapConfig mapConfig) {
        return new ModelRecordContext(new CopyOnWriteArrayList<>(),
                new CopyOnWriteArrayList<>(),
                new CopyOnWriteArrayList<>(),
                new CopyOnWriteArrayList<>(), mapConfig);
    }
}
