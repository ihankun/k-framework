package io.hankun.framework.ai.model.rerank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: KRerankModel
 * @createAt: 2025/6/17 14:57
 * @author: hankun
 */
public interface KRerankModel {

    Logger log = LoggerFactory.getLogger(KRerankModel.class);

    default List<Document> rerankDocuments(String query, List<Document> documents, int topN, double threshold) {
        List<KDocumentWithScore> kDocumentWithScores = rerank(query, documents, topN);
        List<Document> result = new ArrayList<>(kDocumentWithScores.size());
        for (KDocumentWithScore kDocumentWithScore : kDocumentWithScores) {
            log.info("文本：{}，匹配内容：{}，排序评分：{}，向量评分：{}", query, kDocumentWithScore.document().getText(),
                    kDocumentWithScore.score(), kDocumentWithScore.document().getScore());
            if (kDocumentWithScore.score() >= threshold) {
                result.add(kDocumentWithScore.document());
            }
        }
        return result;
    }

    List<KDocumentWithScore> rerank(String query, List<Document> documents, int topN);

}
