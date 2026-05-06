package io.hankun.framework.ai.agent.api.entity;

import io.hankun.framework.ai.model.prompt.PromptInfo;

import java.util.Map;

/**
 * @description:
 * @className: NodeInfoList
 * @createAt: 2025/11/17 16:23
 * @author: hankun
 */
public record NodeInfoList(Map<String, NodeInfo> nodes, Map<String, PromptInfo> prompts) {
}
