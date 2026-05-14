package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.core.context.IContext;
import io.hankun.framework.ai.mem.entity.MemDataVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: MemContext
 * @createAt: 2025/12/4 17:04
 * @author: hankun
 */
public record MemContext(Map<String, List<MemDataVo>> memDatas) implements IContext {

    public void addMem(String key, List<MemDataVo> memData) {
        memDatas.computeIfAbsent(key, k -> new ArrayList<>()).addAll(memData);
    }
}
