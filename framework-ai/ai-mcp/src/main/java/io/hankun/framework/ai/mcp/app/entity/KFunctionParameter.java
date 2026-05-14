package io.hankun.framework.ai.mcp.app.entity;

import lombok.Data;

/**
 * @description:
 * @className: KFunctionParameter
 * @createAt: 2025/5/28 17:12
 * @author: hankun
 */
@Data
public class KFunctionParameter {
    private String name;
    private String schema;
    private String description;
    private boolean required;
}
