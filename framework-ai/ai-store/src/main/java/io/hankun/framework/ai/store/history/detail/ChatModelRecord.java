package io.hankun.framework.ai.store.history.detail;


import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.record.IDetailRecord;
import io.hankun.framework.commons.utils.ContextUtil;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @className: ChatModelRecord
 * @createAt: 2025/10/16 10:30
 * @author: hankun
 */
public record ChatModelRecord(
        String type,
        CurrentId currentId,
        List<Message> inputs,
        List<Message> outputs) implements IDetailRecord {

    public static final String KEY = "chat";

    public static final String OUTPUTS = "outputs";

    @Override
    public void updateWithMeta(Map<String, Object> meta) {
        List<Message> outputs = ContextUtil.getDataList(meta, OUTPUTS);
        if (outputs != null) {
            this.outputs.addAll(outputs);
            meta.remove(OUTPUTS);
        }
    }

    public List<Message> loadOutputs(Map<String, Object> meta) {
        if (meta == null) {
            return this.outputs;
        }
        if (outputs.isEmpty()) {
            List<Message> result = ContextUtil.getDataList(meta, OUTPUTS);
            return Objects.requireNonNullElse(result, this.outputs);
        } else {
            return this.outputs;
        }
    }

    public static ChatModelRecord of(CurrentId currentId, List<Message> inputs, List<Message> outputs) {
        return new ChatModelRecord(KEY, currentId, inputs, outputs);
    }

    public static ChatModelRecord of(CurrentId currentId) {
        return new ChatModelRecord(KEY, currentId, new ArrayList<>(), new ArrayList<>());
    }

}
