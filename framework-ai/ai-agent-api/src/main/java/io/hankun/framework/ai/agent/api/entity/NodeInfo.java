package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: NodeInfo
 * @createAt: 2025/11/17 16:26
 * @author: hankun
 */
@Data
public class NodeInfo {
    @Schema(description = "节点编码")
    private String nodeCode;
    @Schema(description = "模型")
    private String model;
    @Schema(description = "是否流式")
    private Boolean stream;
    @Schema(description = "是否输出")
    private Boolean output;
    @Schema(description = "节点工具")
    private List<String> tools = new ArrayList<>();
    @Schema(description = "提示词编码")
    private String promptCode;
    @Schema(description = "输入格式化")
    private String inputFormat;
    @Schema(description = "扩展参数")
    private List<ExtendConfigInfo> extendConfigs = new ArrayList<>();
    @Schema(description = "用户上下文")
    private List<String> userContexts = new ArrayList<>();

    public record ExtendConfigInfo(String key, String desc, Object value) {
    }
}
