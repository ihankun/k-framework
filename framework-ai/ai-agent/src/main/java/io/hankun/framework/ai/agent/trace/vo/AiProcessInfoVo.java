package io.hankun.framework.ai.agent.trace.vo;

import io.hankun.framework.ai.agent.trace.node.AiProcessInfo;

import java.util.List;

/**
 * @description:
 * @className: AiProcessInfoVo
 * @createAt: 2025/9/1 13:40
 * @author: hankun
 */
public record AiProcessInfoVo(String conversationId, Long startTime, List<AiNodeInfoVo> nodeInfoList) {

    public static AiProcessInfoVo of(AiProcessInfo aiProcessInfo) {
        List<AiNodeInfoVo> nodeInfoVos = aiProcessInfo.getNodeInfoList().stream().map(AiNodeInfoVo::of).toList();
        return new AiProcessInfoVo(aiProcessInfo.getConversationId(), aiProcessInfo.getStartTime(), nodeInfoVos);
    }
}
