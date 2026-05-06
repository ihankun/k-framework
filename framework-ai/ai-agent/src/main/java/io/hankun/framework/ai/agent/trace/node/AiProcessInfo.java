package io.hankun.framework.ai.agent.trace.node;

import io.hankun.framework.ai.tools.trace.detail.DetailLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @className: AiProcessInfo
 * @createAt: 2025/7/8 09:25
 * @author: hankun
 */
@Getter
public class AiProcessInfo {

    private final String conversationId;

    private final Long startTime;

    private final List<AiNodeInfo> nodeInfoList = new ArrayList<>();

    private final DetailLevel detailLevel;

    private static final ReentrantLock lock = new ReentrantLock();

    public AiProcessInfo(String conversationId) {
        this(conversationId, DetailLevel.NONE);
    }

    public AiProcessInfo(String conversationId,
                         DetailLevel detailLevel) {
        this.conversationId = conversationId;
        this.detailLevel = detailLevel;
        startTime = System.currentTimeMillis();
    }

    public Long getFirstReplyTime() {
        if (nodeInfoList.isEmpty()) {
            return System.currentTimeMillis();
        }
        return nodeInfoList.getFirst().getFirstTime();
    }

    public void addNode(AiNodeInfo nodeInfo) {
        lock.lock();
        try {
            nodeInfoList.add(nodeInfo);
        } finally {
            lock.unlock();
        }
    }

    public AiNodeInfo getNodeInfo(String key) {
        for (AiNodeInfo nodeInfo : nodeInfoList) {
            if (nodeInfo.getKey().equals(key)) {
                return nodeInfo;
            }
        }
        return null;
    }

    public void createNode(String nodeId) {
        lock.lock();
        try {
            AiNodeInfo nodeInfo = new AiNodeInfo(nodeId, detailLevel,
                    nodeInfoList.size() + 1, conversationId);
            nodeInfoList.add(nodeInfo);
        } finally {
            lock.unlock();
        }
    }

}
