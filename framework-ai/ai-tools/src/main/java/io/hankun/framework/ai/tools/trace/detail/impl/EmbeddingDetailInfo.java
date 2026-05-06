package io.hankun.framework.ai.tools.trace.detail.impl;

import io.hankun.framework.ai.tools.trace.detail.BaseDetailInfo;
import io.hankun.framework.ai.tools.trace.detail.DetailLevel;
import io.hankun.framework.ai.tools.trace.detail.DetailType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @description:
 * @className: EmbeddingDetailInfo
 * @createAt: 2025/6/30 09:32
 * @author: hankun
 */
@Getter
@Setter
public class EmbeddingDetailInfo extends BaseDetailInfo {

    private String modelName;

    private Integer inputTokens;

    private Integer outputTokens;

    private int textLength;

    private List<String> inputs;

    public EmbeddingDetailInfo(String conversationId, DetailLevel level) {
        super(DetailType.EMBEDDING, conversationId, level);
    }

    @Override
    public String groupKey() {
        return modelName;
    }

    @Override
    public String buildDetailDesc() {
        return "向量化模型：" + modelName + "，输入文本长度：" + textLength;
    }
}
