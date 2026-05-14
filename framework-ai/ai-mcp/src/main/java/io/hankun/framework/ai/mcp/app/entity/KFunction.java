package io.hankun.framework.ai.mcp.app.entity;

import io.hankun.framework.ai.mcp.entity.ToolKey;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @className: KFunction
 * @createAt: 2025/5/28 14:59
 * @author: hankun
 */
@Data
public class KFunction {

    private String name;
    private String httpMethod;
    private String schema;
    private String path;
    private String description;
    private boolean returnDirect;
    private String serviceName;
    private String grayMark;
    private List<KFunctionParameter> parameters;

    public ToolKey buildToolKey() {
        return new ToolKey(schema, name);
    }

}
