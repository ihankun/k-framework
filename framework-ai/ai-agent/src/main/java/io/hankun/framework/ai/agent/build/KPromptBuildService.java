package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.api.AgentLoader;
import io.hankun.framework.ai.agent.api.entity.AgentExtendPrompt;
import io.hankun.framework.ai.agent.api.entity.AgentStructInfo;
import io.hankun.framework.ai.agent.api.entity.PromptDataInfo;
import io.hankun.framework.ai.agent.api.entity.PromptParams;
import io.hankun.framework.ai.agent.node.ActionConfigConvertor;
import io.hankun.framework.ai.model.prompt.PromptInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description:
 * @className: KPromptBuildService
 * @createAt: 2025/12/1 13:43
 * @author: hankun
 */
@ConditionalOnBean(AgentLoader.class)
@Component
public class KPromptBuildService implements PromptBuildService {

    @Lazy
    @Autowired
    private KAgentStructBuildService kAgentStructBuildService;

    @Override
    public PromptInfo getPrompt(String agentCode, String promptCode) {
        KAgentStructBuildService.AgentHolder agentHolder = kAgentStructBuildService.getHolder(agentCode);
        if (agentHolder == null) {
            return null;
        }
        AgentStructInfo agentStructInfo = agentHolder.getAgentStructInfo();
        if (agentStructInfo == null) {
            return null;
        }
        PromptDataInfo targetPrompt = null;
        for (PromptDataInfo promptDataInfo : agentStructInfo.getPrompts()) {
            if (Objects.equals(promptCode, promptDataInfo.getPromptCode())) {
                targetPrompt = promptDataInfo;
            }
        }
        if (targetPrompt == null) {
            return null;
        }
        AgentExtendPrompt extendPrompt = agentHolder.getExtendPrompt();
        if (extendPrompt == null) {
            return null;
        }
        PromptParams targetExtend = null;
        for (PromptParams promptParams : extendPrompt.promptParams()) {
            if (Objects.equals(promptCode, promptParams.promptCode())) {
                targetExtend = promptParams;
            }
        }
        if (targetExtend != null) {
            targetPrompt.getParamsDatas().clear();
            targetPrompt.getParamsDatas().addAll(targetExtend.params());
        }
        PromptInfo promptInfo = ActionConfigConvertor.convert(targetPrompt);
        return kAgentStructBuildService.freshPromptInfo(promptInfo);
    }

}
