package io.hankun.framework.ai.store.history.context;

import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.common.record.TaskRecord;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @description:
 * @className: TaskRecordContext
 * @createAt: 2025/10/16 17:17
 * @author: hankun
 */
public record TaskRecordContext(Map<String, TaskRecord> taskRecords) implements IContext {

    @Nullable
    public TaskRecord load(String agentId) {
        return taskRecords.get(agentId);
    }
}
