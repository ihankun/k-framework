package io.ihankun.framework.log.context;


import io.ihankun.framework.log.entity.ApiLog;

/**
 * 存储请求的来源地址、本次请求地址url
 * @author hankun
 */
public class ApiLogContext {

    public static final String SOURCE_URL_HEADER_NAME = "sourceUrl";
    public static final String PRE_URL_HEADER_NAME = "preUrl";
    public static final String PRE_SERVICE_HEADER_NAME = "preService";
    private static final ThreadLocal<ApiLog> INHERITABLE_CONTEXT_HOLDER = new InheritableThreadLocal<>();

    public ApiLogContext() {
    }

    /**
     * 重置当前线程的数据信息
     */
    public static void reset() {
        INHERITABLE_CONTEXT_HOLDER.remove();
    }

    /**
     * 将ApiLog设置到线程上
     */
    public static void set(ApiLog apiLog) {
        INHERITABLE_CONTEXT_HOLDER.set(apiLog);
    }

    /**
     * 从线程的上下文中获取ApiLog
     */
    public static ApiLog get() {
        return INHERITABLE_CONTEXT_HOLDER.get();
    }
}
