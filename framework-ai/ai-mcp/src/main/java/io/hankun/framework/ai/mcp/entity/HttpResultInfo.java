package io.hankun.framework.ai.mcp.entity;

/**
 * @description:
 * @className: HttpResultInfo
 * @createAt: 2025/8/28 17:31
 * @author: hankun
 */
public record HttpResultInfo(boolean success, boolean exception, String message, String traceId) {
}
