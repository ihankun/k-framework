package io.hankun.framework.ai.agent.build;

import io.hankun.framework.ai.agent.KAgent;

/**
 * @description:
 * @className: AgentBuilder
 * @createAt: 2025/10/24 11:44
 * @author: hankun
 */
public interface AgentBuilder {

    /**
     * agentCode
     *
     * @return agentCode
     */
    String agentCode();

    /**
     * 构建KAgent
     *
     * @param builder builder
     * @return KAgent
     */
    KAgent build(KAgent.Builder builder);
}
