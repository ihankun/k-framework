package io.hankun.framework.ai.agent.node;

import io.hankun.framework.ai.agent.api.entity.*;
import io.hankun.framework.ai.agent.config.KNodeConfig;
import io.hankun.framework.ai.agent.node.config.ActionConfig;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: ActionConfigConvertor
 * @createAt: 2025/11/17 18:31
 * @author: hankun
 */
public class ActionConfigConvertor {
    public static ActionConfig convert(NodeInfo nodeInfo,
                                       KNodeConfig.NodeConfig nodeConfig, PromptInfo promptInfo) {
        Map<String, Object> extendConfigs = new HashMap<>(nodeInfo.getExtendConfigs().size());
        for (NodeInfo.ExtendConfigInfo configInfo : nodeInfo.getExtendConfigs()) {
            extendConfigs.put(configInfo.key(), configInfo.value());
        }
        return ActionConfig.builder()
                .nodeId(nodeInfo.getNodeCode())
                .model(nodeConfig.getModel() == null ? nodeInfo.getModel() : nodeConfig.getModel())
                .stream(nodeConfig.getStream() == null ? nodeInfo.getStream() : nodeConfig.getStream())
                .tools(nodeInfo.getTools())
                .promptCode(nodeInfo.getPromptCode())
                .promptInfo(promptInfo)
                .inputFormat(nodeInfo.getInputFormat())
                .extendConfigs(extendConfigs)
                .userContext(nodeInfo.getUserContexts())
                .build();
    }

    public static NodeInfo convert(ActionConfig actionConfig) {
        NodeInfo nodeConfigInfo = new NodeInfo();
        List<NodeInfo.ExtendConfigInfo> extendConfigInfos = new ArrayList<>(actionConfig.getExtendConfigs().size());
        for (Map.Entry<String, Object> entry : actionConfig.getExtendConfigs().entrySet()) {
            extendConfigInfos.add(new NodeInfo.ExtendConfigInfo(entry.getKey(), "", entry.getValue()));
        }
        nodeConfigInfo.setNodeCode(actionConfig.getNodeId());
        nodeConfigInfo.setModel(actionConfig.getModel());
        nodeConfigInfo.setStream(actionConfig.getStream());
        nodeConfigInfo.setOutput(actionConfig.getOutput());
        nodeConfigInfo.setTools(actionConfig.getTools());
        nodeConfigInfo.setPromptCode(actionConfig.getPromptCode());
        nodeConfigInfo.setInputFormat(actionConfig.getInputFormat());
        nodeConfigInfo.setExtendConfigs(extendConfigInfos);
        nodeConfigInfo.setUserContexts(actionConfig.getUserContext());
        return nodeConfigInfo;
    }

    public static NodeConfigInfo convertConfigInfo(ActionConfig actionConfig) {
        NodeConfigInfo nodeConfigInfo = new NodeConfigInfo();
        nodeConfigInfo.setNodeCode(actionConfig.getNodeId());
        nodeConfigInfo.setNodeDesc(actionConfig.getNodeDesc());
        nodeConfigInfo.setNodeType("");
        nodeConfigInfo.setAiNode(actionConfig.getAiNode());
        NodeInfo nodeInfo = convert(actionConfig);
        nodeConfigInfo.setNodeInfo(nodeInfo);
        return nodeConfigInfo;
    }

    public static Map<String, PromptInfo> convert(List<PromptDataInfo> promptDataInfos) {
        if (CollectionUtils.isEmpty(promptDataInfos)) {
            return Map.of();
        }
        Map<String, PromptInfo> promptInfos = new HashMap<>(promptDataInfos.size());
        for (PromptDataInfo promptDataInfo : promptDataInfos) {
            PromptInfo promptInfo = convert(promptDataInfo);
            promptInfos.put(promptDataInfo.getPromptCode(), promptInfo);
        }
        return promptInfos;
    }

    public static PromptInfo convert(PromptDataInfo promptDataInfo) {
        if (CollectionUtils.isEmpty(promptDataInfo.getParams())) {
            return new PromptInfo(promptDataInfo.getPromptData(),
                    List.of(), Map.of());
        }
        List<String> promptParams = new ArrayList<>(promptDataInfo.getParams().size());
        for (ParamInfo paramInfo : promptDataInfo.getParams()) {
            promptParams.add(paramInfo.getParamKey());
        }
        Map<String, String> promptParamsData = new HashMap<>(promptDataInfo.getParamsDatas().size());
        for (ParamData paramData : promptDataInfo.getParamsDatas()) {
            promptParamsData.put(paramData.getParamKey(), paramData.getParamData());
        }
        return new PromptInfo(promptDataInfo.getPromptData(),
                promptParams, promptParamsData);
    }
}
