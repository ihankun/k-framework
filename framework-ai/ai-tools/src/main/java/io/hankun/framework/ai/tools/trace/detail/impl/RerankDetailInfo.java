package io.hankun.framework.ai.tools.trace.detail.impl;

import io.hankun.framework.ai.model.rerank.KDocumentWithScore;
import io.hankun.framework.ai.tools.trace.detail.BaseDetailInfo;
import io.hankun.framework.ai.tools.trace.detail.DetailLevel;
import io.hankun.framework.ai.tools.trace.detail.DetailType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @description:
 * @className: RerankDetailInfo
 * @createAt: 2025/6/30 15:50
 * @author: hankun
 */
@Getter
@Setter
public class RerankDetailInfo extends BaseDetailInfo {

    private String modelName;

    private Integer inputTokens;

    private Integer outputTokens;

    private String query;

    private List<Document> documents;

    private int topN;

    private List<KDocumentWithScore> result;

    public RerankDetailInfo(String conversationId, DetailLevel level) {
        super(DetailType.RE_RANK, conversationId, level);
    }

    @Override
    public String groupKey() {
        return modelName;
    }

    @Override
    public String buildDetailDesc() {
        return "模型：" + modelName + "，匹配语句：" + query;
    }

    public void setDocuments(List<Document> documents) {
        if (detailLevel.detailThan(DetailLevel.BASE)) {
            this.documents = documents;
        }
    }

    public void setResult(List<KDocumentWithScore> result) {
        if (detailLevel.detailThan(DetailLevel.BASE)) {
            this.result = result;
        }
    }

}
