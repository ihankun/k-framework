package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @className: PromptDataInfo
 * @createAt: 2025/11/14 10:53
 * @author: hankun
 */
@Data
public class PromptDataInfo {
    @Schema(description = "提示词编码")
    private String promptCode;
    @Schema(description = "提示词描述")
    private String promptDesc;
    @Schema(description = "提示词内容")
    private String promptData;
    @Schema(description = "参数列表")
    private List<ParamInfo> params;
    @Schema(description = "参数数据")
    private List<ParamData> paramsDatas;
}
