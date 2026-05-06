package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: KEdgeAction
 * @createAt: 2025/10/21 08:43
 * @author: hankun
 */
public interface KEdgeAction extends KAction {

    String route(@Schema(description = "上下文")
                 @NotNull
                 ContextAccess contextAccess,
                 @Schema(description = "输入")
                 @NotNull
                 InputParams inputParams,
                 @Schema(description = "输出")
                 @NotNull
                 NodeOutput nodeOutput);
}
