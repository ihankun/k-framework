package io.hankun.framework.ai.model.interceptors;

import io.hankun.framework.ai.model.rerank.KDocumentWithScore;
import io.hankun.framework.ai.model.rerank.KRerankModel;
import org.springframework.ai.document.Document;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @description:
 * @className: RerankInterceptor
 * @createAt: 2025/9/1 08:57
 * @author: hankun
 */
public interface RerankInterceptor extends Ordered {

    List<KDocumentWithScore> rerank(KRerankModel rerankModel, String query, List<Document> documents, int topN);
}
