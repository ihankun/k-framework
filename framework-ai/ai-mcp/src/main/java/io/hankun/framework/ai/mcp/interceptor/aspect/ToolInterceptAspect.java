package io.hankun.framework.ai.mcp.interceptor.aspect;

import io.hankun.framework.ai.mcp.interceptor.MsunToolInterceptManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @className: ToolInterceptAspect
 * @createAt: 2025/6/10 15:00
 * @author: hankun
 */
@Aspect
@Component
public class ToolInterceptAspect {

    private final MsunToolInterceptManager msunToolInterceptManager;

    public ToolInterceptAspect(MsunToolInterceptManager msunToolInterceptManager) {
        this.msunToolInterceptManager = msunToolInterceptManager;
    }

    @Around("execution(* org.springframework.ai.tool.ToolCallbackProvider.getToolCallbacks(..))")
    public Object interceptGetToolCallbacks(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行原始方法获取原始结果
        Object originResult = joinPoint.proceed();

        if (originResult instanceof ToolCallback[] originCallbacks) {
            ToolCallback[] modifiedCallbacks = new ToolCallback[originCallbacks.length];
            for (int i = 0; i < originCallbacks.length; i++) {
                ToolCallback originCallback = originCallbacks[i];
                modifiedCallbacks[i] = msunToolInterceptManager.addIntercept(originCallback);
            }
            return modifiedCallbacks;
        } else {
            return originResult;
        }

    }
}
