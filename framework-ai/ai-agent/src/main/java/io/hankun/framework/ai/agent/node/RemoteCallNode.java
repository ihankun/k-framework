package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

/**
 * @description:
 * @className: RemoteCallNode
 * @createAt: 2025/12/10 09:37
 * @author: hankun
 */
public abstract class RemoteCallNode implements KNodeAction {

    @Override
    public KNodeResult exec(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams, @NotNull NodeOutput nodeOutput) {
        Flux<String> call = call(contextAccess, inputParams);
        return KNodeResult.ofText(nodeOutput.emitAndWaitText(call));
    }

    public abstract Flux<String> call(@NotNull ContextAccess contextAccess, @NotNull InputParams inputParams);
}
