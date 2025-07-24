package io.ihankun.framework.springcloud.server.auth;

import com.google.common.base.Splitter;
import io.ihankun.framework.core.context.LoginUserContext;
import io.ihankun.framework.core.context.LoginUserInfo;
import io.ihankun.framework.core.context.UpstreamInfoContext;
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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author hankun
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(value = "kun.product.auth.enable", havingValue = "true")
public class AuthAspect implements ApplicationContextAware {

    private static final String UPSTREAM_GATEWAY = "gateway-login";

    private String applicationName;

    @Resource
    AuthConfiguration configuration;

    private List<Product> authSystem = new ArrayList<>(1);

    @Pointcut("@annotation(io.ihankun.framework.springcloud.server.auth.AuthPoint)")
    public void pointMethod() {

    }

    @Pointcut("@within(io.ihankun.framework.springcloud.server.auth.AuthPoint)")
    public void pointClass() {

    }

    @Around("pointMethod()||pointClass()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        //关闭开关则直接跳过
        if (!configuration.isEnable()) {
            return point.proceed();
        }

        //1、登录用户为空，则跳过拦截
        LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            return point.proceed();
        }

        //2、如果服务名称为网关，则认为此请求是页面请求，则对其进行权限过滤，其他请求则直接跳过后续逻辑
        String upstream = getHeaderValue(UpstreamInfoContext.UPSTREAM);
        if (StringUtils.isEmpty(upstream) || !UPSTREAM_GATEWAY.equals(upstream)) {
            return point.proceed();
        }

        //3、比较当前登录用户允许的系统，和当前接口请求的系统，是否符合
        List<Product> currentSystem = getAllowSystem(point);
        List<String> allowSystemList = Collections.emptyList();
        if (! StringUtils.isEmpty(userInfo.getAllowSystems())) {
            allowSystemList = Splitter.on(",").trimResults().splitToList(userInfo.getAllowSystems());
            for (Product system : currentSystem) {
                if (allowSystemList.contains(system.getValue())) {
                    return point.proceed();
                }
            }
        }

        String error = String.format("此接口请求被拒绝，原因为当前用户没有此接口对应产品的权限,当前用户产品权限=%s,此接口需要产品权限%s,接口路径=%s,请求来源=%s", allowSystemList, currentSystem, getRequestPath(), getHeaderValue(HttpHeaders.REFERER));
        log.info(error);

        //开启限制模式后，判断不通过报错处理
        if (configuration.isLimitMode()) {
            String tipsMsg = String.format("禁止访问，没有此产品权限[%s]，请尝试重新登录", userInfo.getSystemName());
            throw new RuntimeException(tipsMsg);
        }

        return point.proceed();
    }


    /**
     * 获取注解
     *
     * @param point
     * @return
     */
    public List<AuthPoint> getAnnotation(ProceedingJoinPoint point) {

        List<AuthPoint> list = new ArrayList<>(1);
        Class<?> targetClass = point.getTarget().getClass();
        // 尝试获取类级别的注解
        AuthPoint classAnnotation = targetClass.getAnnotation(AuthPoint.class);
        if (classAnnotation != null) {
            list.add(classAnnotation);
        }
        // 获取方法级别的注解
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        AuthPoint methodAnnotation = method.getAnnotation(AuthPoint.class);
        if (methodAnnotation != null) {
            list.add(methodAnnotation);
        }
        return list;
    }

    /**
     * 获取当前请求被允许的系统
     *
     * @param point
     * @return
     */
    public List<Product> getAllowSystem(ProceedingJoinPoint point) {
        Set<Product> set = new HashSet<>(1);
        List<AuthPoint> annotation = getAnnotation(point);
        if (!CollectionUtils.isEmpty(annotation)) {
            for (AuthPoint authPoint : annotation) {
                set.addAll(Arrays.asList(authPoint.value()));
            }
        }
        if (!CollectionUtils.isEmpty(authSystem)) {
            set.addAll(authSystem);
        }
        return new ArrayList<>(set);
    }

    /**
     * 获取请求头内容
     *
     * @param headerName
     * @return
     */
    private String getHeaderValue(String headerName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(headerName);
        }
        return null;
    }

    /**
     * 获取接口请求路径
     *
     * @return
     */
    private String getRequestPath() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            return applicationName + request.getRequestURI();
        }
        return applicationName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IServiceBelongProduct> beans = applicationContext.getBeansOfType(IServiceBelongProduct.class);
        if (beans != null) {
            for (IServiceBelongProduct value : beans.values()) {
                authSystem.addAll(value.belong());
            }
        }
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        log.info("垂直越权切面注入完成,当前服务名称={},归属产品={}", applicationName, authSystem);
    }
}
