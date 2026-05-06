package io.hankun.framework.ai.agent.prompt.impl;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import io.hankun.framework.ai.context.model.ModelContextManageService;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @description:
 * @className: NodePromptService
 * @createAt: 2025/6/11 10:37
 * @author: hankun
 */
@Slf4j
@Component
public class NodePromptService {

    private final ConfigService configService;

    private final Map<String, NodePromptHolder> promptHolderMap = new HashMap<>();

    private final ModelContextManageService modelContextManageService;

    public NodePromptService(ModelContextManageService modelContextManageService) {
        this.modelContextManageService = modelContextManageService;
        this.configService = NacosConfigManager.getInstance().getConfigService();
    }


    public PromptInfo getPrompt(String agentId, String promptCode) {
        String group;
        if (promptCode.startsWith("context")) {
            group = "context";
        } else {
            group = "node";
        }
        NodePromptHolder nodePromptHolder = promptHolderMap.computeIfAbsent(promptCode, new Function<String, NodePromptHolder>() {
            @Override
            public NodePromptHolder apply(String s) {
                return new NodePromptHolder(configService, s, group, modelContextManageService);
            }
        });
        return new PromptInfo(nodePromptHolder.getPrompt(), nodePromptHolder.getParams(), Map.of());
    }
}
