package io.hankun.framework.ai.model.rerank;

import org.springframework.ai.document.Document;

public record KDocumentWithScore(Document document, double score) {
}
