package io.hankun.framework.ai.model.rerank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: MsunRerankModel
 * @createAt: 2025/6/17 14:57
 * @author: hankun
 */
public interface MsunRerankModel {

    Logger log = LoggerFactory.getLogger(MsunRerankModel.class);

    default List<Document> rerankDocuments(String query, List<Document> documents, int topN, double threshold) {
        List<MsunDocumentWithScore> msunDocumentWithScores = rerank(query, documents, topN);
        List<Document> result = new ArrayList<>(msunDocumentWithScores.size());
        for (MsunDocumentWithScore msunDocumentWithScore : msunDocumentWithScores) {
            log.info("文本：{}，匹配内容：{}，排序评分：{}，向量评分：{}", query, msunDocumentWithScore.document().getText(),
                    msunDocumentWithScore.score(), msunDocumentWithScore.document().getScore());
            if (msunDocumentWithScore.score() >= threshold) {
                result.add(msunDocumentWithScore.document());
            }
        }
        return result;
    }

    List<MsunDocumentWithScore> rerank(String query, List<Document> documents, int topN);

}
