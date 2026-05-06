package io.hankun.framework.ai.agent.trace.node;

import io.hankun.framework.ai.tools.trace.detail.BaseDetailInfo;
import io.hankun.framework.ai.tools.trace.detail.DetailLevel;
import io.hankun.framework.ai.tools.trace.detail.DetailTraceList;
import io.hankun.framework.ai.tools.trace.detail.impl.ChatCallDetailInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: AiNodeInfo
 * @createAt: 2025/7/14 14:10
 * @author: hankun
 */
@Getter
public class AiNodeInfo {

    private final String key;

    private final DetailLevel detailLevel;

    private final Long startTime;

    private final int index;

    private final DetailTraceList detailTraceList;

    private Long endTime;

    @Setter
    private String userMessage;

    public AiNodeInfo(String key,
                      DetailLevel detailLevel,
                      int index,
                      String conversationId) {
        this.key = key;
        this.detailLevel = detailLevel;
        this.index = index;
        this.startTime = System.currentTimeMillis();
        this.detailTraceList = new DetailTraceList(key, conversationId, detailLevel);
        this.endTime = startTime;
    }

    public void finish() {
        this.endTime = System.currentTimeMillis();
    }

    public String getMessages(ChatCallDetailInfo callDetailInfo) {
        if (isDisable()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (ChatCallDetailInfo.ResponseNode node : callDetailInfo.getResultNodes()) {
            builder.append(node.getContent());
        }
        return builder.toString();
    }

    public boolean isDisable() {
        return DetailLevel.NONE.equals(detailLevel);
    }

    public List<ChatCallDetailInfo> getAllChatCallDetailInfo() {
        List<ChatCallDetailInfo> result = new ArrayList<>();
        for (BaseDetailInfo detailInfo : detailTraceList.getDetailInfoList()) {
            if (detailInfo instanceof ChatCallDetailInfo chatCallDetailInfo) {
                result.add(chatCallDetailInfo);
            }
        }
        return result;
    }

    public ChatCallDetailInfo getFirstChatCallDetailInfo() {
        for (BaseDetailInfo detailInfo : detailTraceList.getDetailInfoList()) {
            if (detailInfo instanceof ChatCallDetailInfo chatCallDetailInfo) {
                return chatCallDetailInfo;
            }
        }
        return null;
    }

    public ChatCallDetailInfo getLastChatCallDetailInfo() {
        for (int i = detailTraceList.getDetailInfoList().size() - 1; i >= 0; i--) {
            BaseDetailInfo detailInfo = detailTraceList.getDetailInfoList().get(i);
            if (detailInfo instanceof ChatCallDetailInfo chatCallDetailInfo) {
                return chatCallDetailInfo;
            }
        }
        return null;
    }

    public Long getFirstDelay() {
        if (isDisable()) {
            return 0L;
        }
        return getFirstTime() - startTime;
    }

    public Long getFirstTime() {
        if (isDisable()) {
            return startTime;
        }
        ChatCallDetailInfo chatCallDetailInfo = getFirstChatCallDetailInfo();
        if (chatCallDetailInfo == null) {
            return startTime;
        }
        ChatCallDetailInfo.ResponseNode firstResponseNode = getFirstResponseNode(chatCallDetailInfo);
        if (firstResponseNode == null) {
            return startTime;
        }
        return firstResponseNode.getResponseTime();
    }

    public Long getLastDelay() {
        if (isDisable()) {
            return 0L;
        }
        return getLastTime() - startTime;
    }

    public Long getLastTime() {
        if (isDisable()) {
            return startTime;
        }
        ChatCallDetailInfo chatCallDetailInfo = getLastChatCallDetailInfo();
        if (chatCallDetailInfo == null) {
            return startTime;
        }
        ChatCallDetailInfo.ResponseNode lastResponseNode = getLastResponseNode(chatCallDetailInfo);
        if (lastResponseNode == null) {
            return startTime;
        }
        return lastResponseNode.getResponseTime();
    }

    public ChatCallDetailInfo.ResponseNode getFirstResponseNode(ChatCallDetailInfo chatCallDetailInfo) {
        if (isDisable()) {
            return null;
        }
        if (chatCallDetailInfo.getResultNodes().isEmpty()) {
            return null;
        }
        return chatCallDetailInfo.getResultNodes().getFirst();
    }

    public ChatCallDetailInfo.ResponseNode getLastResponseNode(ChatCallDetailInfo chatCallDetailInfo) {
        if (isDisable()) {
            return null;
        }
        if (chatCallDetailInfo.getResultNodes().isEmpty()) {
            return null;
        }
        return chatCallDetailInfo.getResultNodes().getLast();
    }

}
