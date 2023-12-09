package io.ihankun.framework.cache.requestlock.aspect;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.cache.CacheBuilder;
import io.ihankun.framework.cache.CacheManager;
import io.ihankun.framework.cache.key.ICacheKey;
import io.ihankun.framework.cache.key.impl.OrgCacheKey;
import io.ihankun.framework.cache.requestlock.annotation.RequestLock;
import io.ihankun.framework.common.base.BaseService;
import io.ihankun.framework.common.context.LoginUserContext;
import io.ihankun.framework.common.error.IErrorCode;
import io.ihankun.framework.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author hankun
 */
@Component
@Aspect
@Slf4j
@Order
public class RequestLockAspect implements BaseService, Ordered, PriorityOrdered {

    /**
     * 过期时间
     */
    private static final Long TIMEOUT = 15L;

    /**
     * 过期时间单位
     */
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    /**
     * 默认异常前缀
     */
    private static final String DEFAULT_PREFIX = "requestLock";

    /**
     * 默认错误编码
     */
    private static final String DEFAULT_CODE = "0000";

    /**
     * 缓存管理器
     */
//    @Resource
    private CacheManager<String, String> cacheManager = CacheBuilder.build();

    @Pointcut("@annotation(io.ihankun.framework.cache.requestlock.annotation.RequestLock)")
    public void lockPointcut() {

    }

    @Around("lockPointcut()")
    public Object around(ProceedingJoinPoint point) {
        ICacheKey lockKey = getOrgCacheKey(point);

        //如果其他线程正在执行的话，抛出异常信息
        if (!cacheManager.string().setIfAbsent(lockKey, BigDecimal.ZERO.toString(), TIMEOUT, TIMEOUT_UNIT)) {
            throw BusinessException.build(getErrorCode(point));
        }
        try {
            return point.proceed(point.getArgs());
        } catch (BusinessException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        } finally {
            //最后清空缓存
            cacheManager.string().del(lockKey);
        }
    }


    /**
     * 获取请求锁的key
     *
     * @param point
     * @return
     */
    private ICacheKey getOrgCacheKey(ProceedingJoinPoint point) {
        StringBuilder sb = new StringBuilder()
                .append(point.getSignature().toLongString())
                .append(JSON.toJSONString(Arrays.toString(point.getArgs())))
                .append(LoginUserContext.get());
        return OrgCacheKey.build(DEFAULT_PREFIX).orgId(String.valueOf(getOrgId())).key(DigestUtils.md5DigestAsHex(sb.toString().getBytes()));
    }

    /**
     * 根据注解信息生成ErrorCode信息
     *
     * @param point
     * @return
     */
    private IErrorCode getErrorCode(ProceedingJoinPoint point) {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        String value = method.getAnnotation(RequestLock.class).value();

        if (StringUtils.isEmpty(value)) {
            value = RequestLock.MESSAGE;
        }

        String finalValue = value;
        return new IErrorCode() {
            @Override
            public String prefix() {
                return DEFAULT_PREFIX;
            }

            @Override
            public String getCode() {
                return DEFAULT_CODE;
            }

            @Override
            public String getMsg() {
                return finalValue;
            }
        };
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
