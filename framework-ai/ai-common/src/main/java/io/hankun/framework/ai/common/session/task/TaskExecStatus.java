package io.hankun.framework.ai.common.session.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: TaskExecStatus
 * @createAt: 2025/10/16 14:20
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum TaskExecStatus {
    INIT("init", "初始化"),
    SUBMITTED("submitted", "已提交"),
    WORKING("working", "执行中"),
    INPUT_REQUIRED("input-required", "需要用户输入"),
    AUTH_REQUIRED("auth-required", "需要授权"),
    COMPLETED("completed", "完成", true),
    CANCELED("canceled", "取消", true),
    FAILED("failed", "失败", true),
    REJECTED("rejected", "拒绝", true),
    UNKNOWN("unknown", "未知", true);

    private final String code;

    private final String desc;

    private final boolean isFinal;

    TaskExecStatus(String code, String desc) {
        this(code, desc, false);
    }

    public static TaskExecStatus getByCode(String state) {
        return switch (state) {
            case "submitted" -> SUBMITTED;
            case "working" -> WORKING;
            case "input-required" -> INPUT_REQUIRED;
            case "auth-required" -> AUTH_REQUIRED;
            case "completed" -> COMPLETED;
            case "canceled" -> CANCELED;
            case "failed" -> FAILED;
            case "rejected" -> REJECTED;
            case "unknown" -> UNKNOWN;
            default -> throw new IllegalArgumentException("Invalid TaskExecStatus: " + state);
        };
    }
}
