package io.hankun.framework.ai.model.interceptors;

import io.hankun.framework.ai.model.rerank.MsunDocumentWithScore;
import io.hankun.framework.ai.model.rerank.MsunRerankModel;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @description:
 * @className: MsunRerankModelProxy
 * @createAt: 2025/8/29 17:14
 * @author: hankun
 */
public class MsunRerankModelProxy implements MsunRerankModel {

    private final MsunRerankModel msunRerankModel;

    private final RerankInterceptor rerankInterceptor;

    public MsunRerankModelProxy(MsunRerankModel msunRerankModel,
                                RerankInterceptor rerankInterceptor) {
        this.msunRerankModel = msunRerankModel;
        this.rerankInterceptor = rerankInterceptor;
    }

    @Override
    public List<MsunDocumentWithScore> rerank(String query, List<Document> documents, int topN) {
        return rerankInterceptor.rerank(msunRerankModel, query, documents, topN);
    }
}
