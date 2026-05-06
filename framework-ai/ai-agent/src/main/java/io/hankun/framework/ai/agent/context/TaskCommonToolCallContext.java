package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.store.history.detail.TaskToolCallRecord;

import java.util.List;

/**
 * @description:
 * @className: TaskCommonToolCallContext
 * @createAt: 2025/10/28 14:55
 * @author: hankun
 */
public record TaskCommonToolCallContext(List<TaskToolCallRecord> records) implements IContext {
}
