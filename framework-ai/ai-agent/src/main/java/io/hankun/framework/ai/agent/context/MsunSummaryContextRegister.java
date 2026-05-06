package io.hankun.framework.ai.agent.context;

import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.register.NoBuildContextRegister;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: MsunSummaryContextRegister
 * @createAt: 2025/12/5 14:42
 * @author: hankun
 */
@Component
public class MsunSummaryContextRegister implements NoBuildContextRegister<MemSummaryContext> {
    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.MESSAGE_IN_AGENT;
    }

    @Override
    public String desc() {
        return "记忆构建";
    }

    @NotNull
    @Override
    public Class<MemSummaryContext> dataType() {
        return MemSummaryContext.class;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
