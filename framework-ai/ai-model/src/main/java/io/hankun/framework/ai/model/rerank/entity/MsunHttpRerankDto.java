package io.hankun.framework.ai.model.rerank.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @className: MsunHttpRerankDto
 * @createAt: 2025/7/7 10:12
 * @author: hankun
 */
@NoArgsConstructor
@Data
public class MsunHttpRerankDto {
    private String model;
    private String query;
    private List<String> documents;
}
