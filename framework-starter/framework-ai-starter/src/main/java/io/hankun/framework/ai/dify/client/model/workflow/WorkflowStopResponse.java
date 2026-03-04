package io.hankun.framework.ai.dify.client.model.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作流停止响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStopResponse {
    /**
     * 结果
     */
    private String result;
}
