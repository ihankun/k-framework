package io.hankun.framework.ai.common.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: OperateStatus
 * @createAt: 2025/10/30 16:03
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum OperateStatus {

    START("start", "开始", false),

    WORKING("working", "执行中", false),

    COMPLETE("complete", "完成", true),

    CANCEL("cancel", "取消", true),

    FAILED("failed", "失败", true),

    ;
    private final String code;
    private final String desc;
    private final boolean isFinal;

}
