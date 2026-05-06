package io.hankun.framework.ai.agent.node.config;

import io.hankun.framework.ai.agent.node.entity.NextNodeInfo;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import io.hankun.framework.ai.tools.advisors.AdvisorConfig;
import io.hankun.framework.ai.tools.client.ClientConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: ActionConfig
 * @createAt: 2025/10/21 08:53
 * @author: hankun
 */
@Getter
@Builder
@ToString
public class ActionConfig {
    private String nodeId;
    private String nodeDesc;
    private String model;
    private Boolean stream;
    private Boolean output;
    private Boolean aiNode;
    @Builder.Default
    private List<String> tools = new ArrayList<>();
    private String promptCode;
    private PromptInfo promptInfo;
    @ToString.Exclude
    private ClientConfig clientConfig;
    private String inputFormat;
    @Builder.Default
    private Map<String, Object> extendConfigs = new HashMap<>();
    @Builder.Default
    private List<String> userContext = new ArrayList<>();
    @Builder.Default
    private NextNodeInfo nextNodeInfo = new NextNodeInfo();


    public void withAiNode(Boolean aiNode) {
        this.aiNode = aiNode;
    }

    public void withNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public static ActionConfig merge(ActionConfig actionConfig, ActionConfig override) {
        if (override == null) {
            return actionConfig;
        }
        if (actionConfig == null) {
            return override;
        }
        List<String> userContext = new ArrayList<>();
        if (!CollectionUtils.isEmpty(actionConfig.getUserContext())) {
            userContext.addAll(actionConfig.getUserContext());
        }
        if (!CollectionUtils.isEmpty(override.getUserContext())) {
            for (String expand : override.getUserContext()) {
                if (!userContext.contains(expand)) {
                    userContext.add(expand);
                }
            }
        }
        List<String> tools = new ArrayList<>();
        if (!CollectionUtils.isEmpty(actionConfig.getTools())) {
            tools.addAll(actionConfig.getTools());
        }
        if (!CollectionUtils.isEmpty(override.getTools())) {
            for (String tool : override.getTools()) {
                if (!tools.contains(tool)) {
                    tools.add(tool);
                }
            }
        }
        Map<String, Object> extendConfigs = new HashMap<>();
        if (!CollectionUtils.isEmpty(actionConfig.getExtendConfigs())) {
            extendConfigs.putAll(actionConfig.getExtendConfigs());
        }
        if (!CollectionUtils.isEmpty(override.getExtendConfigs())) {
            extendConfigs.putAll(override.getExtendConfigs());
        }
        return ActionConfig.builder()
                .nodeId(actionConfig.getNodeId())
                .nodeDesc(override.getNodeDesc() == null ? actionConfig.getNodeDesc() : override.getNodeDesc())
                .model(override.getModel() == null ? actionConfig.getModel() : override.getModel())
                .stream(override.getStream() == null ? actionConfig.getStream() : override.getStream())
                .aiNode(override.getAiNode() == null ? actionConfig.getAiNode() : override.getAiNode())
                .tools(tools)
                .output(override.getOutput() == null ? actionConfig.getOutput() : override.getOutput())
                .promptCode(override.getPromptCode() == null ? actionConfig.getPromptCode() : override.getPromptCode())
                .promptInfo(override.getPromptInfo() == null ? actionConfig.getPromptInfo() : override.getPromptInfo())
                .clientConfig(ClientConfig.merge(actionConfig.getClientConfig(), override.getClientConfig()))
                .extendConfigs(extendConfigs)
                .inputFormat(override.getInputFormat() == null ? actionConfig.getInputFormat() : override.getInputFormat())
                .userContext(userContext)
                .nextNodeInfo(actionConfig.getNextNodeInfo())
                .build();
    }

    public static ActionConfig build(String nodeId, String model, Boolean stream) {
        return ActionConfig.builder()
                .nodeId(nodeId)
                .model(model)
                .stream(stream)
                .promptCode("node-" + nodeId + "-prompt.txt")
                .clientConfig(new ClientConfig(AdvisorConfig.buildSimple()))
                .build();
    }

}
