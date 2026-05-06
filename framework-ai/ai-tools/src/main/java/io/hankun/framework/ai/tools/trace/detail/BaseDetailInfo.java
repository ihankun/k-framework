package io.hankun.framework.ai.tools.trace.detail;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: BaseDetailInfo
 * @createAt: 2025/6/30 09:28
 * @author: hankun
 */
@Getter
public abstract class BaseDetailInfo {
    private final DetailType detailType;
    private final String conversationId;
    protected final DetailLevel detailLevel;
    protected Long startTime = 0L;
    protected Long endTime = 0L;

    private final Map<String, Object> context = new HashMap<>();

    protected BaseDetailInfo(DetailType detailType, String conversationId, DetailLevel detailLevel) {
        this.detailType = detailType;
        this.conversationId = conversationId;
        this.detailLevel = detailLevel;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        endTime = null;
    }

    public void end() {
        endTime = System.currentTimeMillis();
    }

    public String buildCostTime() {
        return (startTime - endTime) + "ms";
    }

    public void setContext(Map<String, Object> context) {
        this.context.putAll(context);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContextData(String key) {
        return (T) context.get(key);
    }


    public abstract String groupKey();

    public abstract String buildDetailDesc();

    @Override
    public String toString() {
        return "type【" + detailType.getDesc() + "】,desc【" + buildDetailDesc() + "】,cost【" + buildCostTime() + "】,conversationId=" + conversationId;
    }

}
