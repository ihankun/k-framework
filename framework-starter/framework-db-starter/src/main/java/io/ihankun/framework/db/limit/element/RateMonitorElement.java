package io.ihankun.framework.db.limit.element;

import io.ihankun.framework.db.limit.RateLimitStrategyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hankun
 */
@Data
@AllArgsConstructor
public class RateMonitorElement {


    /**
     * 偏移量，每隔一个心跳+1，每满一个周期后如果没有被限流则释放
     * 偏移量越大，则说明该元素距离上次被监视的时间越久
     */
    private Integer offset;

    /**
     * 策略
     */
    private RateLimitStrategyEnum strategy;

    /**
     * 监视对象
     */
    private String monitorKey;

    /**
     * 监视计数
     */
    private int count;
}
