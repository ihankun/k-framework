package io.ihankun.framework.core.context;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StringUtils;

/**
 * @author hankun
 */
@Slf4j
public class DomainContext {

    public static final String DOMAIN_HEADER_NAME = "domain";

    /**
     * 线程上下文
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new NamedThreadLocal<>(DOMAIN_HEADER_NAME);

    /**
     * 获取当前请求的访问域名
     */
    public static String get() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除域名信息
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
        MDC.remove(DOMAIN_HEADER_NAME);
    }

    /**
     * 模拟域名信息
     */
    public static void mock(String domain) {
        if (!StringUtils.isEmpty(domain)) {
            CONTEXT_HOLDER.set(domain);
            MDC.putCloseable(DOMAIN_HEADER_NAME, domain);
        }
    }
}
