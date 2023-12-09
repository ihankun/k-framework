package io.ihankun.framework.cache.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */

@Getter
@AllArgsConstructor
public enum TimeoutTypeEnum {
    FOREVER(1), // 永不超时
    DYNAMIC(2), // 动态超时
    FIXED(3); // 固定超时

    /**
     * 类型
     */
    @JsonValue
    private final Integer type;
}
