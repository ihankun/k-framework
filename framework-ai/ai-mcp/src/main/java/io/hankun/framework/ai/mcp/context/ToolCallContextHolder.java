package io.hankun.framework.ai.mcp.context;

import io.hankun.framework.ai.mcp.entity.HttpResultInfo;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: ToolCallContextHolder
 * @createAt: 2025/8/29 08:51
 * @author: hankun
 */
public class ToolCallContextHolder {

    public static final String TOOL_CALL_RECORDS = "TOOL_CALL_RECORDS";

    public static void init() {
        KContext kContext = KContextHolder.get();
        if (kContext == null) {
            return;
        }
        getToolCallRecords(kContext).clear();
    }

    public static void addToolCallbackRecord(ToolCallRecord toolCallRecord) {
        KContext kContext = KContextHolder.get();
        if (kContext == null) {
            return;
        }
        getToolCallRecords(kContext).add(toolCallRecord);
    }

    public static List<ToolCallRecord> getRecords() {
        KContext kContext = KContextHolder.get();
        if (kContext == null) {
            return null;
        }
        return getToolCallRecords(kContext);
    }

    private static List<ToolCallRecord> getToolCallRecords(@NotNull KContext kContext) {
        List<ToolCallRecord> toolCallRecords = kContext.getDataList(TOOL_CALL_RECORDS);
        if (toolCallRecords == null) {
            toolCallRecords = new ArrayList<>();
            kContext.setData(TOOL_CALL_RECORDS, toolCallRecords);
        }
        return toolCallRecords;
    }

    public static void setLastResultInfo(HttpResultInfo resultInfo) {
        ToolCallRecord lastToolCallRecord = getLastToolCallRecord();
        if (lastToolCallRecord != null) {
            lastToolCallRecord.setResultInfo(resultInfo);
        }
    }

    public static ToolCallRecord getLastToolCallRecord() {
        KContext kContext = KContextHolder.get();
        if (kContext == null) {
            return null;
        }
        List<ToolCallRecord> toolCallRecords = getToolCallRecords(kContext);
        if (toolCallRecords.isEmpty()) {
            return null;
        }
        return toolCallRecords.getLast();
    }
}
