package io.hankun.framework.ai.agent.api;

import io.hankun.framework.ai.agent.api.entity.AgentDesc;
import io.hankun.framework.ai.agent.api.entity.AgentExtendPrompt;
import io.hankun.framework.ai.agent.api.entity.AgentStructInfo;

import java.util.List;

/**
 * @description:
 * @className: AgentLoader
 * @createAt: 2025/11/27 09:09
 * @author: hankun
 */
public interface AgentLoader {

    List<AgentDesc> getAllAgentCodes(String serviceCode, String serviceVersion);

    AgentStructInfo load(String serviceCode, String agentCode, String agentVersion);

    AgentExtendPrompt getPromptParams(String serviceCode, String agentCode);
}
