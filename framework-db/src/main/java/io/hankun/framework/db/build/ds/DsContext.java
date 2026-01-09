package io.hankun.framework.db.build.ds;

import org.slf4j.MDC;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StringUtils;

/**
 * @author hankun
 */
public class DsContext {

    public static final String DS_HEADER_NAME = "dataSource";

    /**
     * 线程上下文
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new NamedThreadLocal<>(DS_HEADER_NAME);

    public static String getDataSourceName() {
        return CONTEXT_HOLDER.get();
    }


    public static void setDataSourceName(String ds) {
        if (!StringUtils.isEmpty(ds)) {
            CONTEXT_HOLDER.set(ds);
        }
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
        MDC.remove(DS_HEADER_NAME);
    }
}
