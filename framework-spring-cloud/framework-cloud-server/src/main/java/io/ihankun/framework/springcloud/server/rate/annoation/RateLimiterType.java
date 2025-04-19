package io.ihankun.framework.springcloud.server.rate.annoation;

/**
 * @author hankun
 */
public enum RateLimiterType {

    ByUserId,
    ByUserSysId,
    ByMethod,
    ByCustom
}
