package io.ihankun.framework.db.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;

@Slf4j
public class DbCreateErrorContext {
    public static final String ERROR_MESSAGE = "message";

    /**
     * 线程上下文
     */
    private static final ThreadLocal<StringBuilder> CONTEXT_HOLDER = new NamedThreadLocal<>(ERROR_MESSAGE);


    public static StringBuilder get() {
        return CONTEXT_HOLDER.get();
    }

    public static void init() {
        if (CONTEXT_HOLDER.get() == null) {
            StringBuilder stringBuilder = new StringBuilder();
            CONTEXT_HOLDER.set(stringBuilder);
        }
    }


    public static void error(String... errMessages) {
        if (CONTEXT_HOLDER.get() == null) {
            log.info("DbCreateErrorContext.not.active,message={}", (Object) errMessages);
            return;
        }
        try {
            if (errMessages != null && errMessages.length != 0) {
                StringBuilder stringBuilder = CONTEXT_HOLDER.get();

                stringBuilder.append("[");
                for (String message : errMessages) {
                    stringBuilder.append(message);
                }
                stringBuilder.append("]");
            }
        } catch (Throwable e) {
            log.info("DbCreateErrorContext.set.failed,message={}", (Object) errMessages);
        }

    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
