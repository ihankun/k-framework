package io.ihankun.framework.ai.dify.client.model.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ihankun.framework.ai.dify.client.model.common.Metadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本生成响应（阻塞模式）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponse {
    /**
     * 消息唯一 ID
     */
    private String messageId;

    /**
     * App 模式，固定为 chat
     */
    private String mode;

    /**
     * 完整回复内容
     */
    private String answer;

    /**
     * 元数据
     */
    private Metadata metadata;

    /**
     * 消息创建时间戳
     */
    private Long createdAt;
}
