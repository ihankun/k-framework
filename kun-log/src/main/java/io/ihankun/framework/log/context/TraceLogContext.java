package io.ihankun.framework.log.context;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.NamedThreadLocal;

/**
 * @author hankun
 */
public class TraceLogContext {
    /**
     * 日志类型
     */
    public static final String LOG_TYPE = "logType";

    public static final String REQUEST_URI = "requestUri";

    public static final String TRACE_HEADER_NAME = "traceId";

    private static final ThreadLocal<String> INHERITABLE_CONTEXT_HOLDER = new NamedThreadLocal<>("trace-log-context");

    /**
     * 重置当前线程的数据信息
     */
    public static void reset() {
        INHERITABLE_CONTEXT_HOLDER.remove();
        ThreadContext.remove("traceId");
    }

    /**
     * 将traceId设置到线程上
     *
     * @param traceId 跟踪id
     */
    public static void set(String traceId) {
        INHERITABLE_CONTEXT_HOLDER.set(traceId);
        //将loginUserInfo信息放入log4j2的线程上下文中，用于日志打印时输出相关信息
        ThreadContext.put("traceId", traceId);
    }

    /**
     * 从线程的上下文中获取traceId
     *
     * @return
     */
    public static String get() {
        return INHERITABLE_CONTEXT_HOLDER.get();
    }
}
