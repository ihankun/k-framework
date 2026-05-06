package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.model.prompt.PromptInfo;

/**
 * @description:
 * @className: PromptBuildService
 * @createAt: 2025/12/1 13:41
 * @author: hankun
 */
public interface PromptBuildService {

    PromptInfo getPrompt(String agentCode, String promptCode);
}
