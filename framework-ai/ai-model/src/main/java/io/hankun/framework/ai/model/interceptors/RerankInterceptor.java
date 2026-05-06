package io.hankun.framework.ai.model.interceptors;

import io.hankun.framework.ai.model.rerank.MsunDocumentWithScore;
import io.hankun.framework.ai.model.rerank.MsunRerankModel;
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

    List<MsunDocumentWithScore> rerank(MsunRerankModel rerankModel, String query, List<Document> documents, int topN);
}
