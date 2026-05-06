package io.hankun.framework.ai.model.rerank;

import org.springframework.ai.document.Document;

public record MsunDocumentWithScore(Document document, double score) {
}
