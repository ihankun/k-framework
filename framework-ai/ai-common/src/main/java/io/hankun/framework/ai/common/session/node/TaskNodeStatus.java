package io.hankun.framework.ai.common.session.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: TaskNodeStatus
 * @createAt: 2025/10/17 17:10
 * @author: hankun
 */
@AllArgsConstructor
@Getter
public enum TaskNodeStatus {

    STARTED("started", "开始", false),

    WORKING("working", "执行中", false),

    COMPLETED("completed", "完成", true),

    FAILED("failed", "失败", true),

    ;

    private final String code;

    private final String desc;

    private final boolean isFinal;
}
