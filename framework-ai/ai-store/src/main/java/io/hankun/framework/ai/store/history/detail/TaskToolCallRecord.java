package io.hankun.framework.ai.store.history.detail;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.core.record.IDetailRecord;

/**
 * @description:
 * @className: TaskToolCallRecord
 * @createAt: 2025/10/16 10:38
 * @author: hankun
 */
public record TaskToolCallRecord(String type, CurrentId currentId, String toolName, String params,
                                 String result) implements IDetailRecord {

    public static final String KEY = "toolCall";

    public static TaskToolCallRecord of(CurrentId currentId, String toolName, String params,
                                        String result) {
        return new TaskToolCallRecord(KEY, currentId, toolName, params, result);
    }

}
