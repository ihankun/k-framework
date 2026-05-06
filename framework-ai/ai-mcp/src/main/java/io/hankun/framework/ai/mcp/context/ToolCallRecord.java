package io.hankun.framework.ai.mcp.context;

import io.hankun.framework.ai.mcp.entity.HttpResultInfo;
import io.hankun.framework.commons.utils.ContextUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: ToolCallRecord
 * @createAt: 2025/8/28 17:02
 * @author: hankun
 */
@Getter
public class ToolCallRecord {
    private final String toolName;
    private final boolean returnDirect;
    @Setter
    private String toolInput;
    @Setter
    private String toolOutput;
    @Setter
    private long startTime;
    @Setter
    private long endTime;

    private final Map<String, Object> context = new HashMap<>();

    public ToolCallRecord(ToolCallback toolCallback) {
        this(toolCallback.getToolDefinition(), toolCallback.getToolMetadata());
    }

    public ToolCallRecord(ToolDefinition toolDefinition, ToolMetadata toolMetadata) {
        this.toolName = toolDefinition.name();
        this.returnDirect = toolMetadata.returnDirect();
    }

    public void startCall(String toolInput) {
        this.startTime = System.currentTimeMillis();
        this.toolInput = toolInput;
    }

    public void endCall(String toolOutput) {
        this.endTime = System.currentTimeMillis();
        this.toolOutput = toolOutput;
    }

    public void setResultInfo(HttpResultInfo resultInfo) {
        ContextUtil.setData(context, "resultInfo", resultInfo);
    }

    public HttpResultInfo getResultInfo() {
        return ContextUtil.getData(this.context, "resultInfo");
    }

}
