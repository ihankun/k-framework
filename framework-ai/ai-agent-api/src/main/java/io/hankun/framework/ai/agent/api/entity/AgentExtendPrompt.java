package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @description:
 * @className: AgentExtendPrompt
 * @createAt: 2025/11/28 11:29
 * @author: hankun
 */
public record AgentExtendPrompt(
        @Schema(description = "agent编码")
        String agentCode,
        @Schema(description = "扩展提示词版本")
        String extendVersion,
        @Schema(description = "扩展提示词参数")
        List<PromptParams> promptParams) {
}
