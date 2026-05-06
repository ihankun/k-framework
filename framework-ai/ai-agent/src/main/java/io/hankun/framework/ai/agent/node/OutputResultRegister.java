package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.context.entity.ContextLifecycle;
import io.hankun.framework.ai.context.register.NoBuildContextRegister;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: OutputResultRegister
 * @createAt: 2025/11/5 10:21
 * @author: hankun
 */
@Component
public class OutputResultRegister implements NoBuildContextRegister<NodeOutput> {
    @NotNull
    @Override
    public ContextLifecycle lifecycle() {
        return ContextLifecycle.MESSAGE;
    }

    @Override
    public String desc() {
        return "输出结果";
    }

    @NotNull
    @Override
    public Class<NodeOutput> dataType() {
        return NodeOutput.class;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
