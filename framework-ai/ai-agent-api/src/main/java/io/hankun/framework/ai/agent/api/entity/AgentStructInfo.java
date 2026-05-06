package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: AgentStructInfo
 * @createAt: 2025/11/27 09:09
 * @author: hankun
 */
@Data
public class AgentStructInfo {
    @Schema(description = "服务编码")
    private String serviceCode;
    @Schema(description = "agent编码")
    private String agentCode;
    @Schema(description = "agent描述")
    private String agentDesc;
    @Schema(description = "分类")
    private List<String> categories;
    @Schema(description = "流程图")
    private GraphData graphData;
    @Schema(description = "上下文")
    private List<String> contexts = new ArrayList<>();
    @Schema(description = "代理节点配置")
    private List<NodeConfigInfo> configs;
    @Schema(description = "代理提示词")
    private List<PromptDataInfo> prompts;
    @Schema(description = "版本号")
    private String version;
}
