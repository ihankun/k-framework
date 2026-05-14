package io.hankun.framework.ai.store.history.po;

import io.hankun.framework.ai.core.record.IDetailRecord;
import io.hankun.framework.ai.core.record.RecordHolder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: ModelHistory
 * @createAt: 2025/10/16 16:57
 * @author: hankun
 */
@Data
@Document(collection = "modelHistory")
public class ModelHistory {
    @Id
    private ObjectId id;

    private String type;

    private String agentId;

    @Indexed
    private String messageId;

    @Indexed
    private String sessionId;

    @Indexed
    private String taskId;

    private IDetailRecord modelRecord;

    private Map<String, Object> meta;

    public static <T extends IDetailRecord> ModelHistory of(RecordHolder<T> record) {
        ModelHistory data = new ModelHistory();
        data.setId(new ObjectId());
        data.setType(record.record().type());
        data.setAgentId(record.record().currentId().agentId());
        data.setMessageId(record.record().currentId().messageId());
        data.setSessionId(record.record().currentId().sessionId());
        data.setTaskId(record.record().currentId().taskId());
        data.setModelRecord(record.record());
        data.setMeta(record.meta());
        return data;
    }

    public static <T extends IDetailRecord> List<ModelHistory> of(List<RecordHolder<T>> records) {
        if (CollectionUtils.isEmpty(records)) {
            return List.of();
        }
        List<ModelHistory> data = new ArrayList<>(records.size());
        for (RecordHolder<T> record : records) {
            record.record().updateWithMeta(record.meta());
            data.add(of(record));
        }
        return data;
    }
}
