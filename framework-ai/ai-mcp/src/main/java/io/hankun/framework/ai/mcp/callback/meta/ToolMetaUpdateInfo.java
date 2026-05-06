package io.hankun.framework.ai.mcp.callback.meta;

import io.hankun.framework.ai.mcp.entity.ToolKey;

/**
 * @description:
 * @className: ToolMetaUpdateInfo
 * @createAt: 2025/7/21 14:05
 * @author: hankun
 */
public record ToolMetaUpdateInfo(ToolKey toolKey, boolean isReturnDirect) {
}
