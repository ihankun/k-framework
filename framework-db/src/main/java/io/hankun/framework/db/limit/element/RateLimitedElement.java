package io.hankun.framework.db.limit.element;

import io.hankun.framework.db.limit.RateLimitStrategyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hankun
 */
@Data
@AllArgsConstructor
public class RateLimitedElement {
    /**
     * 限流策略
     */
    private RateLimitStrategyEnum limitStrategy;

    /**
     * 限流值，根据不同的策略组装不同的值
     */
    private String limitedKey;

    /**
     * 限流释放时间
     */
    private Long expireTime;
}

