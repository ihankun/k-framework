package io.hankun.framework.ai.store.history.context;

import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.core.record.ChatRecord;

import java.util.List;

/**
 * @description:
 * @className: ChatRecordContext
 * @createAt: 2025/11/3 14:58
 * @author: hankun
 */
public record ChatRecordContext(List<ChatRecord> history,
                                ChatRecord currentRecord) implements IContext {
}
