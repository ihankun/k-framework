package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.api.entity.*;
import io.hankun.framework.ai.agent.config.KNodeConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: NodeConfigService
 * @createAt: 2025/11/17 14:21
 * @author: hankun
 */
@Component
public class NodeConfigService {

    private final KNodeConfig kNodeConfig;

    public NodeConfigService(KNodeConfig kNodeConfig) {
        this.kNodeConfig = kNodeConfig;
    }

    public Map<String, ActionConfig> getActionConfigMap(Map<String, NodeInfo> nodes, Map<String, PromptInfo> prompts) {
        Map<String, ActionConfig> agentConfig = new HashMap<>(nodes.size());
        for (Map.Entry<String, NodeInfo> entry : nodes.entrySet()) {
            NodeInfo nodeConfigInfo = entry.getValue();
            PromptInfo promptInfo = prompts.get(nodeConfigInfo.getPromptCode());
            if (promptInfo == null) {
                throw new RuntimeException("promptInfo not found");
            }
            KNodeConfig.NodeConfig nodeConfig = kNodeConfig.loadConfig(nodeConfigInfo.getNodeCode());
            ActionConfig actionConfig = ActionConfigConvertor.convert(nodeConfigInfo, nodeConfig, promptInfo);
            agentConfig.put(nodeConfigInfo.getNodeCode(), actionConfig);
        }
        return agentConfig;
    }

    private static NodeInfoList create(ConfigData configData) {
        Map<String, NodeInfo> nodes = new HashMap<>();
        for (Map.Entry<String, ActionConfig> entry : configData.configs().entrySet()) {
            ActionConfig actionConfig = entry.getValue();
            NodeInfo nodeConfigInfo = ActionConfigConvertor.convert(actionConfig);
            nodes.put(actionConfig.getNodeId(), nodeConfigInfo);
        }
        return new NodeInfoList(nodes, configData.prompts());
    }


    private ConfigData build(AgentConfigData load) {
        Map<String, ActionConfig> agentConfig = new HashMap<>();
        Map<String, PromptInfo> prompts = buildPromptInfo(load);
        for (NodeConfigInfo nodeConfigInfo : load.getConfigs()) {
            PromptInfo promptInfo = prompts.get(nodeConfigInfo.getNodeInfo().getPromptCode());
            if (promptInfo == null) {
                throw new RuntimeException("promptInfo not found");
            }
            KNodeConfig.NodeConfig nodeConfig = kNodeConfig.loadConfig(nodeConfigInfo.getNodeCode());
            ActionConfig actionConfig = ActionConfigConvertor.convert(nodeConfigInfo.getNodeInfo(), nodeConfig, promptInfo);
            agentConfig.put(nodeConfigInfo.getNodeCode(), actionConfig);
        }
        return new ConfigData(agentConfig, prompts);
    }

    private static Map<String, PromptInfo> buildPromptInfo(AgentConfigData load) {
        Map<String, PromptInfo> prompts = new HashMap<>();
        for (PromptDataInfo promptInfo : load.getPrompts()) {
            List<String> promptParams = new ArrayList<>();
            for (ParamInfo paramInfo : promptInfo.getParams()) {
                promptParams.add(paramInfo.getParamKey());
            }
            Map<String, String> promptParamsData = new HashMap<>();
            for (ParamData paramData : promptInfo.getParamsDatas()) {
                promptParamsData.put(paramData.getParamKey(), paramData.getParamData());
            }
            prompts.put(promptInfo.getPromptCode(), new PromptInfo(promptInfo.getPromptCode(),
                    promptParams, promptParamsData));
        }
        return prompts;
    }

    public record ConfigData(Map<String, ActionConfig> configs, Map<String, PromptInfo> prompts) {

    }
}
