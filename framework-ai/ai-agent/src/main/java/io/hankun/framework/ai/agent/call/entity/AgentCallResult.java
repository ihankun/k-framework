package io.hankun.framework.ai.agent.call.entity;

import io.hankun.framework.ai.store.history.vo.UserChatVo;

/**
 * @description:
 * @className: AgentCallResult
 * @createAt: 2025/12/10 09:39
 * @author: hankun
 */
public record AgentCallResult(AgentCallKey agentCallKey, UserChatVo userChatVo) {
}
