package io.hankun.framework.ai.model.interceptors;

import io.hankun.framework.ai.model.rerank.KDocumentWithScore;
import io.hankun.framework.ai.model.rerank.KRerankModel;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @description:
 * @className: KRerankModelProxy
 * @createAt: 2025/8/29 17:14
 * @author: hankun
 */
public class KRerankModelProxy implements KRerankModel {

    private final KRerankModel kRerankModel;

    private final RerankInterceptor rerankInterceptor;

    public KRerankModelProxy(KRerankModel kRerankModel,
                             RerankInterceptor rerankInterceptor) {
        this.kRerankModel = kRerankModel;
        this.rerankInterceptor = rerankInterceptor;
    }

    @Override
    public List<KDocumentWithScore> rerank(String query, List<Document> documents, int topN) {
        return rerankInterceptor.rerank(kRerankModel, query, documents, topN);
    }
}
