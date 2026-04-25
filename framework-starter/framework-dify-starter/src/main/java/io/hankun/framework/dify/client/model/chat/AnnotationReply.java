package io.hankun.framework.dify.client.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标注回复
 *
 * @author zhangriguang
 * @date 2025-05-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationReply {
    /**
     * 任务 ID
     */
    private String jobId;

    /**
     * 任务状态
     */
    private String jobStatus;

    /**
     * 错误信息
     */
    private String errorMsg;
}
