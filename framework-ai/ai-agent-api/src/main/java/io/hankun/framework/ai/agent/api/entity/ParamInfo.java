package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @className: ParamInfo
 * @createAt: 2025/11/14 10:50
 * @author: hankun
 */
@Data
public class ParamInfo {
    @Schema(description = "参数key")
    private String paramKey;
    @Schema(description = "参数描述")
    private String paramDesc;
    @Schema(description = "参数是否可修改")
    private Boolean allowModify;
}
