package io.hankun.framework.ai.tools.trace.detail.impl;

import io.hankun.framework.ai.mcp.context.ToolCallRecord;
import io.hankun.framework.ai.tools.trace.detail.BaseDetailInfo;
import io.hankun.framework.ai.tools.trace.detail.DetailLevel;
import io.hankun.framework.ai.tools.trace.detail.DetailType;
import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @className: McpDetailInfo
 * @createAt: 2025/6/30 15:21
 * @author: hankun
 */
@Setter
@Getter
public class McpDetailInfo extends BaseDetailInfo {

    private String toolName;

    private String param;

    private String result;

    private boolean resultDirect;

    public McpDetailInfo(String conversationId, DetailLevel level) {
        super(DetailType.MCP_CALL, conversationId, level);
    }

    public void convertFrom(ToolCallRecord toolCallRecord) {
        this.toolName = toolCallRecord.getToolName();
        this.resultDirect = toolCallRecord.isReturnDirect();
        this.startTime = toolCallRecord.getStartTime();
        this.endTime = toolCallRecord.getEndTime();
        this.param = toolCallRecord.getToolInput();
        if (detailLevel.detailThan(DetailLevel.BASE)) {
            this.result = toolCallRecord.getToolOutput();
            setContext(toolCallRecord.getContext());
        }
    }

    @Override
    public String groupKey() {
        return toolName;
    }

    @Override
    public String buildDetailDesc() {
        return "MCP调用" + toolName;
    }
}
