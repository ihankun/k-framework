package io.hankun.framework.ai.model.rerank.impl;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.core.entity.HttpResult;
import io.hankun.framework.ai.core.http.KHttpClient;
import io.hankun.framework.ai.model.config.KRerankHttpConfig;
import io.hankun.framework.ai.model.rerank.KDocumentWithScore;
import io.hankun.framework.ai.model.rerank.KRerankModel;
import io.hankun.framework.ai.model.rerank.entity.KHttpRerankDto;
import io.hankun.framework.ai.model.rerank.entity.KHttpRerankVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.ai.document.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @description:
 * @className: KHttpRerankService
 * @createAt: 2025/7/7 10:00
 * @author: hankun
 */
@Slf4j
@ConditionalOnProperty(name = "k.ai.model.rerankModelType",
        havingValue = "io.hankun.framework.ai.model.rerank.impl.KHttpRerankService",
        matchIfMissing = true)
@Component
public class KHttpRerankService implements KRerankModel {


    private final KRerankHttpConfig kRerankHttpConfig;

    private final KHttpClient kHttpClient;

    public KHttpRerankService(KRerankHttpConfig kRerankHttpConfig) {
        this.kRerankHttpConfig = kRerankHttpConfig;
        this.kHttpClient = new KHttpClient(kRerankHttpConfig.getClient());
    }

    @Override
    public List<KDocumentWithScore> rerank(String query, List<Document> documents, int topN) {

        String url = kRerankHttpConfig.getBaseUrl();
        KHttpRerankDto kHttpRerankDto = new KHttpRerankDto();
        kHttpRerankDto.setModel(kHttpRerankDto.getModel());
        kHttpRerankDto.setQuery(query);
        kHttpRerankDto.setDocuments(new ArrayList<>(documents.size()));
        for (Document document : documents) {
            kHttpRerankDto.getDocuments().add(JSON.toJSONString(document));
        }
        Request request = kHttpClient.post(url, JSON.toJSONString(kHttpRerankDto), new Consumer<Request.Builder>() {
            @Override
            public void accept(Request.Builder builder) {
                builder.addHeader("Authorization", "Bearer " + kRerankHttpConfig.getApiKey());
            }
        });
        HttpResult httpResult = kHttpClient.call(request, false);
        if (httpResult.success()) {
            KHttpRerankVo result = JSON.parseObject(httpResult.data(), KHttpRerankVo.class);
            log.debug("重排序结果:{}", result);
            List<KHttpRerankVo.ResultsDTO> results = result.getResults();
            List<KDocumentWithScore> rerankedDocuments = new ArrayList<>(topN);
            for (int i = 0; i < topN; i++) {
                KHttpRerankVo.ResultsDTO resultsDTO = results.get(i);
                Document document = documents.get(resultsDTO.getIndex());
                rerankedDocuments.add(new KDocumentWithScore(document, resultsDTO.getRelevanceScore()));
            }
            return rerankedDocuments;
        }
        log.error("重排序失败:{}", httpResult.data());
        List<KDocumentWithScore> rerankedDocuments = new ArrayList<>(documents.size());
        for (Document document : documents) {
            rerankedDocuments.add(new KDocumentWithScore(document, 0.0));
        }
        return rerankedDocuments;
    }


}
