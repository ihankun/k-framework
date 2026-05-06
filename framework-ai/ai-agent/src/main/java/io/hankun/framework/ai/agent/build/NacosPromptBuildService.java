package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.api.AgentLoader;
import io.hankun.framework.ai.agent.prompt.impl.NodePromptService;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: NacosPromptBuildService
 * @createAt: 2025/12/1 13:41
 * @author: hankun
 */
@ConditionalOnMissingBean(AgentLoader.class)
@Component
public class NacosPromptBuildService implements PromptBuildService {

    private final NodePromptService nodePromptService;

    public NacosPromptBuildService(NodePromptService nodePromptService) {
        this.nodePromptService = nodePromptService;
    }

    @Override
    public PromptInfo getPrompt(String agentCode, String promptCode) {
        return nodePromptService.getPrompt(agentCode, promptCode);
    }
}
