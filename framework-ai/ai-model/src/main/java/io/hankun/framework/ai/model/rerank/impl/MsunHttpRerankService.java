package io.hankun.framework.ai.model.rerank.impl;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.common.entity.HttpResult;
import io.hankun.framework.ai.common.http.KHttpClient;
import io.hankun.framework.ai.model.config.MsunRerankHttpConfig;
import io.hankun.framework.ai.model.rerank.MsunDocumentWithScore;
import io.hankun.framework.ai.model.rerank.MsunRerankModel;
import io.hankun.framework.ai.model.rerank.entity.MsunHttpRerankDto;
import io.hankun.framework.ai.model.rerank.entity.MsunHttpRerankVo;
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
 * @className: MsunHttpRerankService
 * @createAt: 2025/7/7 10:00
 * @author: hankun
 */
@Slf4j
@ConditionalOnProperty(name = "msun.ai.model.rerankModelType",
        havingValue = "com.msun.ai.model.rerank.impl.MsunHttpRerankService",
        matchIfMissing = true)
@Component
public class MsunHttpRerankService implements MsunRerankModel {


    private final MsunRerankHttpConfig msunRerankHttpConfig;

    private final KHttpClient kHttpClient;

    public MsunHttpRerankService(MsunRerankHttpConfig msunRerankHttpConfig) {
        this.msunRerankHttpConfig = msunRerankHttpConfig;
        this.kHttpClient = new KHttpClient(msunRerankHttpConfig.getClient());
    }

    @Override
    public List<MsunDocumentWithScore> rerank(String query, List<Document> documents, int topN) {

        String url = msunRerankHttpConfig.getBaseUrl();
        MsunHttpRerankDto msunHttpRerankDto = new MsunHttpRerankDto();
        msunHttpRerankDto.setModel(msunHttpRerankDto.getModel());
        msunHttpRerankDto.setQuery(query);
        msunHttpRerankDto.setDocuments(new ArrayList<>(documents.size()));
        for (Document document : documents) {
            msunHttpRerankDto.getDocuments().add(JSON.toJSONString(document));
        }
        Request request = kHttpClient.post(url, JSON.toJSONString(msunHttpRerankDto), new Consumer<Request.Builder>() {
            @Override
            public void accept(Request.Builder builder) {
                builder.addHeader("Authorization", "Bearer " + msunRerankHttpConfig.getApiKey());
            }
        });
        HttpResult httpResult = kHttpClient.call(request, false);
        if (httpResult.success()) {
            MsunHttpRerankVo result = JSON.parseObject(httpResult.data(), MsunHttpRerankVo.class);
            log.debug("重排序结果:{}", result);
            List<MsunHttpRerankVo.ResultsDTO> results = result.getResults();
            List<MsunDocumentWithScore> rerankedDocuments = new ArrayList<>(topN);
            for (int i = 0; i < topN; i++) {
                MsunHttpRerankVo.ResultsDTO resultsDTO = results.get(i);
                Document document = documents.get(resultsDTO.getIndex());
                rerankedDocuments.add(new MsunDocumentWithScore(document, resultsDTO.getRelevanceScore()));
            }
            return rerankedDocuments;
        }
        log.error("重排序失败:{}", httpResult.data());
        List<MsunDocumentWithScore> rerankedDocuments = new ArrayList<>(documents.size());
        for (Document document : documents) {
            rerankedDocuments.add(new MsunDocumentWithScore(document, 0.0));
        }
        return rerankedDocuments;
    }


}
