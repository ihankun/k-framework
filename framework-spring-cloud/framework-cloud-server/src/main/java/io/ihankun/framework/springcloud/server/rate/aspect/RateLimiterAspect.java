package io.ihankun.framework.springcloud.server.rate.aspect;

import io.ihankun.framework.core.context.LoginUserContext;
import io.ihankun.framework.core.context.LoginUserInfo;
import io.ihankun.framework.core.exception.BusinessException;
import io.ihankun.framework.springcloud.server.rate.RateLimiterConfiguration;
import io.ihankun.framework.springcloud.server.rate.annoation.RateLimiter;
import io.ihankun.framework.springcloud.server.rate.annoation.RateLimiterType;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * @author hankun
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    private final Map<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>(2000);

    @Resource
    private RateLimiterConfiguration config;

    @Around("@annotation(io.ihankun.framework.springcloud.server.rate.annoation.RateLimiter)")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        //默认开启，防止意外情况，增加配置开关
        if (config == null || !config.isEnabled()) {
            return point.proceed();
        }

        MethodSignature signature = (MethodSignature) point.getSignature();
        RateLimiter annotation = signature.getMethod().getAnnotation(RateLimiter.class);
        if (annotation == null) {
            log.info("限流注解未获取到");
            return point.proceed();
        }

        String originKey = getLimitKeyByType(annotation.type(), signature, annotation.key());

        if (!StringUtils.hasText(originKey)) {
            return point.proceed();
        }

        String key = DigestUtils.md5DigestAsHex(originKey.getBytes(StandardCharsets.UTF_8));
        if (!StringUtils.hasText(key)) {
            return point.proceed();
        }

        Semaphore semaphore = semaphoreMap.computeIfAbsent(key, s -> new Semaphore(annotation.qps()));
        boolean allow = semaphore.tryAcquire();
        try {
            if (allow) {
                return point.proceed();
            }
            log.error("触发产品自定义限流策略,key={},qps={}", originKey, annotation.qps());
            throw BusinessException.build("rate", "9999", annotation.errorMsg());
        } finally {
            if (allow) {
                semaphore.release();
            }
            if (semaphoreMap.size() >= config.getLimiterMaxSize()) {
                log.info("产品自定义限流策略清理,超过最大队列限制,{}", config.getLimiterMaxSize());
                semaphoreMap.clear();
            }
        }
    }

    private String getLimitKeyByType(RateLimiterType type, MethodSignature signature, String customKey) {

        String path = String.format("%s_%s_%s", this.getClass().getName(), signature.getMethod().getName(), String.join("", signature.getParameterNames()));
        if (RateLimiterType.ByUserId.equals(type)) {
            return getUserId() == null ? null : getUserId() + "_" + path;
        }

        if (RateLimiterType.ByMethod.equals(type)) {
            return path;
        }

        if (RateLimiterType.ByCustom.equals(type)) {
            return customKey;
        }


        return null;
    }

    private String getUserId() {
        LoginUserInfo loginUserInfo = LoginUserContext.get();
        return loginUserInfo != null ? (loginUserInfo.getUserId() != null ? loginUserInfo.getUserId().toString() : null) : null;
    }
}
