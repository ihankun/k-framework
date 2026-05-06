package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.node.entity.KNodeResult;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: MsunNodeAction
 * @createAt: 2025/10/23 15:48
 * @author: hankun
 */
public interface KNodeAction extends KAction {

    KNodeResult exec(@Schema(description = "上下文")
                        @NotNull
                        ContextAccess contextAccess,
                     @Schema(description = "输入")
                        @NotNull
                        InputParams inputParams,
                     @Schema(description = "输出")
                        @NotNull
                        NodeOutput nodeOutput);

}
