package io.hankun.framework.ai.agent.call.entity;

import java.util.Map;

/**
 * @description:
 * @className: AgentCallParams
 * @createAt: 2025/12/11 16:17
 * @author: hankun
 */
public record AgentCallParams(String input, Map<String, Object> params) {
}
