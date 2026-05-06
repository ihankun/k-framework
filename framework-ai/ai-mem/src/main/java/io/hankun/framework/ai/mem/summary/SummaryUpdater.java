package io.hankun.framework.ai.mem.summary;

import io.hankun.framework.ai.mem.entity.MemData;

import java.util.List;

/**
 * @description:
 * @className: SummaryUpdater
 * @createAt: 2025/12/4 16:04
 * @author: hankun
 */
@FunctionalInterface
public interface SummaryUpdater {

    MemData updateSummary(List<MemData> facts, List<MemData> history, MemData beforeSummary);
}
