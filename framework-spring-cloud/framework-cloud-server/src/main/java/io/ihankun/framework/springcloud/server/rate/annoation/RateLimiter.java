package io.ihankun.framework.springcloud.server.rate.annoation;

import java.lang.annotation.*;

/**
 * @author hankun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RateLimiter {


    /**
     * 每秒最多并发数
     *
     * @return 并发数
     */
    int qps() default 200;

    /**
     * 限流类型
     *
     * @return 限流类型
     */
    RateLimiterType type() default RateLimiterType.ByUserId;

    /**
     * 自定义key，只有上述type为custom时有效
     *
     * @return key
     */
    String key() default "";

    /**
     * 错误提示信息
     *
     * @return msg
     */
    String errorMsg() default "请求太频繁,请稍后重试";


}
