package io.hankun.framework.ai.common.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: SessionStatus
 * @createAt: 2025/10/16 16:32
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum SessionStatus {

    OPEN("open", "开启", false),

    WORKING("working", "执行中", false),

    CLOSED("closed", "关闭", true),

    FAILED("failed", "失败", true),

    ;
    private final String code;
    private final String desc;
    private final boolean isFinal;

}
