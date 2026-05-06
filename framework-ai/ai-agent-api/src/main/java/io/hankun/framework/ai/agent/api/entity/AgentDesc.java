package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @description:
 * @className: AgentDesc
 * @createAt: 2025/11/28 08:51
 * @author: hankun
 */
public record AgentDesc(
        @Schema(description = "服务编码")
        String serviceCode,
        @Schema(description = "agent编码")
        String agentCode,
        @Schema(description = "agent描述")
        String desc,
        @Schema(description = "agent版本")
        String version,
        @Schema(description = "扩展提示词版本")
        String extendVersion) {
}
