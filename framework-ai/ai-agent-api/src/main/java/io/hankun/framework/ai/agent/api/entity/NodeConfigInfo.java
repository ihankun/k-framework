package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @className: NodeConfigInfo
 * @createAt: 2025/11/14 10:57
 * @author: hankun
 */
@Data
public class NodeConfigInfo {
    @Schema(description = "节点编码")
    private String nodeCode;
    @Schema(description = "节点描述")
    private String nodeDesc;
    @Schema(description = "是否AI节点")
    private Boolean aiNode;
    @Schema(description = "节点类型")
    private String nodeType;
    @Schema(description = "节点配置")
    private NodeInfo nodeInfo;
}
