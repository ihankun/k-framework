package io.hankun.framework.springcloud.server.auth;

import io.hankun.framework.core.context.UpstreamInfoContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hankun
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(value = "kun.product.auth.force", havingValue = "true")
public class AuthForceAspect implements ApplicationContextAware {

    private static final String UPSTREAM_GATEWAY = "gateway-login";

    private String applicationName;
    @Resource
    AuthConfiguration configuration;

    @Pointcut("@within(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        String upstream = getHeaderValue(UpstreamInfoContext.UPSTREAM);
        if (StringUtils.isEmpty(upstream) || !UPSTREAM_GATEWAY.equals(upstream)) {
            return point.proceed();
        }

        Class<?> targetClass = point.getTarget().getClass();
        // 尝试获取类级别的注解
        AuthPoint classAnnotation = targetClass.getAnnotation(AuthPoint.class);
        if (classAnnotation != null) {
            return point.proceed();
        }
        // 获取方法级别的注解
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        AuthPoint methodAnnotation = method.getAnnotation(AuthPoint.class);
        if (methodAnnotation != null) {
            return point.proceed();
        }

        //检查配置的放行地址正则表达式
        if (configuration.getIgnoreRegex() != null && ignoreForce(getRequestPath())) {
            return point.proceed();
        }


        String error = String.format("请求被拦截,原因为:此接口为前端直接调用接口,但未增加@AuthPoint注解进行权限控制,位置:%s,%s()", targetClass.getName(), method.getName());
        log.error(error);
        throw new RuntimeException(error);

    }

    /**
     * 忽略检查的请求匹配
     *
     * @param path
     * @return
     */
    private boolean ignoreForce(String path) {
        List<String> regexList = configuration.getIgnoreRegex();
        if (CollectionUtils.isEmpty(regexList)) {
            return false;
        }
        for (String regex : regexList) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(path);
            while (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取请求头内容
     *
     * @param headerName
     * @return
     */
    private String getHeaderValue(String headerName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader(headerName);
    }

    /**
     * 获取接口请求路径
     *
     * @return
     */
    private String getRequestPath() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return applicationName + request.getRequestURI();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("垂直越权强制检查开启");
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
    }
}
