package io.ihankun.framework.log.context;

import io.ihankun.framework.log.entity.ApiInfo;
import org.springframework.core.NamedThreadLocal;

/**
 * @author hankun
 */
public class TraceLogApiInfoContext {
    public static final String TRACE_HEADER_NAME = "traceApiInfo";

    private static final ThreadLocal<ApiInfo> INHERITABLE_CONTEXT_HOLDER = new NamedThreadLocal<>("trace-log-api-context");

    /**
     * 重置当前线程的数据信息
     */
    public static void reset() {
        INHERITABLE_CONTEXT_HOLDER.remove();
    }

    /**
     * 将traceId设置到线程上
     *
     * @param apiInfo 跟踪id
     */
    public static void set(ApiInfo apiInfo) {
        INHERITABLE_CONTEXT_HOLDER.set(apiInfo);
    }

    /**
     * 从线程的上下文中获取traceId
     */
    public static ApiInfo get() {
        return INHERITABLE_CONTEXT_HOLDER.get();
    }
}
