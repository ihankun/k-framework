package io.hankun.framework.ai.mcp.app.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @className: KFunctionList
 * @createAt: 2025/5/29 09:10
 * @author: hankun
 */
@Data
public class KFunctionList {
    private String serviceName;
    private String grayMark;
    private String version;
    private Date startTime;
    private List<KFunction> functions;
}
