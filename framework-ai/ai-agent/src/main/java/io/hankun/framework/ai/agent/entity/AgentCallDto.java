package io.hankun.framework.ai.agent.entity;

import lombok.Data;

import java.util.Map;

/**
 * @description:
 * @className: AgentCallDto
 * @createAt: 2025/12/22 11:02
 * @author: hankun
 */
@Data
public class AgentCallDto {
    private String agentCode;
    private String sessionId;
    private String messageId;
    private String input;
    private Map<String, Object> params;
    private Map<String, Object> config;
}
