package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.common.context.IContext;
import io.hankun.framework.ai.mem.entity.MemData;

import java.util.List;

/**
 * @description:
 * @className: MemSummaryContext
 * @createAt: 2025/12/5 10:42
 * @author: hankun
 */
public record MemSummaryContext(List<MemData> facts, List<MemData> history, MemData beforeSummary) implements IContext {
}
