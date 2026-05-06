package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @className: ParamData
 * @createAt: 2025/11/14 10:54
 * @author: hankun
 */
@Data
public class ParamData {
    @Schema(description = "参数key")
    private String paramKey;
    @Schema(description = "参数数据")
    private String paramData;
}
