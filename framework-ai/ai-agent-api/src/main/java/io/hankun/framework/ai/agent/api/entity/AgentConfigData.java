package io.hankun.framework.ai.agent.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: AgentConfigData
 * @createAt: 2025/11/14 10:54
 * @author: hankun
 */
@Data
public class AgentConfigData {
    @Schema(description = "服务编码")
    private String serviceCode;
    @Schema(description = "代理编码")
    private String agentCode;
    @Schema(description = "代理描述")
    private String agentDesc;
    @Schema(description = "分类")
    private List<String> categories;
    @Schema(description = "代理流程图")
    private String agentPlantUML;
    @Schema(description = "代理流程图数据")
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
