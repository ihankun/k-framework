package io.hankun.framework.gateway.context;

import org.springframework.core.NamedThreadLocal;

public class TraceIdContext {

    private static final ThreadLocal<String> TRACE_ID_THREAD_LOCAL = new NamedThreadLocal<>("trace");

    public static final String TRACE_ID_HEADER_NAME = "traceId";

    public static String get() {
        return TRACE_ID_THREAD_LOCAL.get();
    }

    public static void mock(String traceId) {
        TRACE_ID_THREAD_LOCAL.set(traceId);
    }

    public static void clear() {
        TRACE_ID_THREAD_LOCAL.remove();
    }
}
