package io.ihankun.framework.db.limit;

import io.ihankun.framework.db.limit.element.RateSnapshotElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@Getter
@AllArgsConstructor
public enum RateLimitStrategyEnum {

    SOURCE_DOMAIN_USER(0, "资源+域名+用户"), SOURCE_USER(1, "资源+用户"),

    SOURCE_DOMAIN(2, "资源+域名"), SOURCE(3, "资源"),

    USER(4, "用户");


    /**
     * 优先级顺序，越小优先级越高
     */
    private final Integer order;

    private final String desc;

    private static final String SPLIT = "_";


    /**
     * 根据策略值和内容，返回不同策略下组装的内容
     *
     * @param strategy
     * @param element
     * @return
     */
    public static String getLimitValue(RateLimitStrategyEnum strategy, RateSnapshotElement element) {
        String source = element.getSource();
        String domain = element.getDomain();
        String userId = element.getUserId();
        switch (strategy) {
            case SOURCE_USER:
                return source + SPLIT + userId;
            case SOURCE_DOMAIN:
                return source + SPLIT + domain;
            case SOURCE:
                return source;
            case USER:
                return userId;
            case SOURCE_DOMAIN_USER:
            default:
                return source + SPLIT + domain + SPLIT + userId;
        }
    }
}
