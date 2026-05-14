package io.hankun.framework.ai.agent.node.entity;

import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.node.NodeOutput;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.entity.InputParams;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

/**
 * @description:
 * @className: KNodeParams
 * @createAt: 2025/12/8 16:12
 * @author: hankun
 */
public record KNodeParams(
        @Schema(description = "当前状态id")
        @NotNull
        CurrentId currentId,
        @Schema(description = "上下文")
        @NotNull
        ContextAccess contextAccess,
        @Schema(description = "输入参数")
        @NotNull
        InputParams inputParams,
        @Schema(description = "输出")
        @NotNull
        NodeOutput nodeOutput,
        @Schema(description = "节点配置")
        @NotNull
        ActionConfig actionConfig) {

    public TaskExecConfig getTaskExecConfig() {
        return TaskExecConfig.of(inputParams);
    }
}
