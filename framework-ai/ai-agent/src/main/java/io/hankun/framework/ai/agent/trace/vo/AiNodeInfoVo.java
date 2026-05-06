package io.hankun.framework.ai.agent.trace.vo;

import io.hankun.framework.ai.agent.trace.node.AiNodeInfo;
import io.hankun.framework.ai.tools.trace.detail.BaseDetailInfo;

import java.util.List;

/**
 * @description:
 * @className: AiNodeInfoVo
 * @createAt: 2025/9/1 13:41
 * @author: hankun
 */
public record AiNodeInfoVo(String key, int index,
                           Long startTime, Long endTime,
                           String userMessage,
                           List<BaseDetailInfo> detailInfoList) {

    public static AiNodeInfoVo of(AiNodeInfo nodeInfo) {
        return new AiNodeInfoVo(nodeInfo.getKey(), nodeInfo.getIndex(),
                nodeInfo.getStartTime(), nodeInfo.getEndTime(),
                nodeInfo.getUserMessage(),
                nodeInfo.getDetailTraceList().getDetailInfoList());
    }
}
