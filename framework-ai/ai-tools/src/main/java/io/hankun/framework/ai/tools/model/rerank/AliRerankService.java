package io.hankun.framework.ai.tools.model.rerank;

import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import com.alibaba.cloud.ai.document.DocumentWithScore;
import com.alibaba.cloud.ai.model.RerankModel;
import com.alibaba.cloud.ai.model.RerankRequest;
import com.alibaba.cloud.ai.model.RerankResponse;
import io.hankun.framework.ai.model.rerank.MsunDocumentWithScore;
import io.hankun.framework.ai.model.rerank.MsunRerankModel;
import io.hankun.framework.ai.tools.trace.TraceContext;
import io.hankun.framework.ai.tools.trace.TraceContextHolder;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: AliRerankService
 * @createAt: 2025/7/15 15:38
 * @author: hankun
 */
@Component
public class AliRerankService implements MsunRerankModel {

    public static final String MODEL_NAME = "gte-rerank-v2";
    private final RerankModel rerankModel;

    public AliRerankService(RerankModel rerankModel) {
        this.rerankModel = rerankModel;
    }

    public List<MsunDocumentWithScore> rerank(String query, List<Document> documents, int topN) {
        RerankResponse response = rerankModel.call(new RerankRequest(query, documents, DashScopeRerankOptions.builder()
                .withModel(MODEL_NAME).withTopN(topN).build()));
        List<DocumentWithScore> documentWithScores = response.getResults();
        List<MsunDocumentWithScore> result = new ArrayList<>(documentWithScores.size());
        for (DocumentWithScore documentWithScore : documentWithScores) {
            result.add(new MsunDocumentWithScore(documentWithScore.getOutput(), documentWithScore.getScore()));
        }
        TraceContext traceContext = TraceContextHolder.peek();
        if (traceContext != null) {
            Usage usage = response.getMetadata().getUsage();
            traceContext.setInputTokens(usage.getPromptTokens());
            traceContext.setOutputTokens(usage.getCompletionTokens());
            traceContext.setTotalTokens(usage.getTotalTokens());
            traceContext.setModelName(MODEL_NAME);
        }
        return result;
    }
}
