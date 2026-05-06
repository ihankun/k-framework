package io.hankun.framework.ai.tools.trace.detail;

import io.hankun.framework.ai.tools.trace.detail.impl.ChatCallDetailInfo;
import io.hankun.framework.ai.tools.trace.detail.impl.EmbeddingDetailInfo;
import io.hankun.framework.ai.tools.trace.detail.impl.McpDetailInfo;
import io.hankun.framework.ai.tools.trace.detail.impl.RerankDetailInfo;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description:
 * @className: DetailTraceList
 * @createAt: 2025/7/15 17:16
 * @author: hankun
 */
@Getter
public class DetailTraceList {

    private final String conversationId;

    private final String key;

    private final List<BaseDetailInfo> detailInfoList = new CopyOnWriteArrayList<>();

    private final DetailLevel detailLevel;

    private final DetailTraceList parent;

    private final List<DetailTraceList> children = new CopyOnWriteArrayList<>();

    public DetailTraceList(String key, String conversationId, DetailLevel detailLevel) {
        this(key, conversationId, detailLevel, null);
    }


    public DetailTraceList(String key, String conversationId, DetailLevel detailLevel, DetailTraceList parent) {
        this.key = key;
        this.conversationId = conversationId;
        this.detailLevel = detailLevel;
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public DetailTraceList createChildDetailTraceList(String key) {
        return new DetailTraceList(key, conversationId, detailLevel, this);
    }

    public ChatCallDetailInfo createChatCallDetailInfo() {
        ChatCallDetailInfo detailInfo = new ChatCallDetailInfo(conversationId, detailLevel);
        detailInfoList.add(detailInfo);
        return detailInfo;
    }

    public EmbeddingDetailInfo createEmbeddingDetailInfo() {
        EmbeddingDetailInfo detailInfo = new EmbeddingDetailInfo(conversationId, detailLevel);
        detailInfoList.add(detailInfo);
        return detailInfo;
    }

    public McpDetailInfo createMcpDetailInfo() {
        McpDetailInfo detailInfo = new McpDetailInfo(conversationId, detailLevel);
        detailInfoList.add(detailInfo);
        return detailInfo;
    }

    public RerankDetailInfo createRerankDetailInfo() {
        RerankDetailInfo detailInfo = new RerankDetailInfo(conversationId, detailLevel);
        detailInfoList.add(detailInfo);
        return detailInfo;
    }
}
