package io.hankun.framework.ai.model.rerank.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @className: KHttpRerankVo
 * @createAt: 2025/7/7 10:12
 * @author: hankun
 */
@NoArgsConstructor
@Data
public class KHttpRerankVo {

    private String id;

    private String model;

    private UsageDTO usage;

    private List<ResultsDTO> results;

    @NoArgsConstructor
    @Data
    public static class UsageDTO {
        @JSONField(name = "total_tokens")
        private Integer totalTokens;
    }

    @NoArgsConstructor
    @Data
    public static class ResultsDTO {

        private Integer index;

        private DocumentDTO document;

        @JSONField(name = "relevance_score")
        private Double relevanceScore;

        @NoArgsConstructor
        @Data
        public static class DocumentDTO {
            private String text;
        }
    }
}
