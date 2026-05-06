package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @description:
 * @className: PromptParams
 * @createAt: 2025/11/28 11:01
 * @author: hankun
 */
public record PromptParams(
        @Schema(description = "提示词编码")
        String promptCode,
        @Schema(description = "提示词参数")
        List<ParamData> params) {
}
